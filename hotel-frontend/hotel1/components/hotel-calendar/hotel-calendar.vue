<template>
  <view class="calendar-popup" v-if="show">
    <view class="mask" @click="close"></view>
    
    <view class="calendar-content">
      <view class="header">
        <text class="close-btn" @click="close">✕</text>
        <text class="title">选择入离日期</text>
      </view>
      
      <view class="week-bar">
        <text v-for="(w, index) in ['日', '一', '二', '三', '四', '五', '六']" :key="index">{{ w }}</text>
      </view>
      
      <scroll-view scroll-y class="scroll-view">
        <view class="month-block" v-for="(m, mIndex) in monthList" :key="mIndex">
          <view class="month-title">{{ m.year }}年{{ m.month }}月</view>
          <view class="day-grid">
            <view class="day-item empty" v-for="e in m.emptyDays" :key="'empty'+e"></view>
            <view class="day-item"
                  v-for="(d, dIndex) in m.days" :key="dIndex"
                  :class="{
                    'disabled': d.disabled,
                    'start': d.date === checkIn,
                    'end': d.date === checkOut,
                    'in-range': isInRange(d.date)
                  }"
                  @click="selectDay(d)">
              <text class="day-txt">{{ isToday(d.date) ? '今天' : d.day }}</text>
              <text class="desc-txt" v-if="d.date === checkIn">入住</text>
              <text class="desc-txt" v-if="d.date === checkOut">离店</text>
            </view>
          </view>
        </view>
      </scroll-view>
      
      <view class="footer">
        <button class="confirm-btn" :class="{'disabled': !checkIn || !checkOut}" @click="confirm">
          完成 <text v-if="checkIn && checkOut" style="font-size: 24rpx; margin-left: 10rpx;">(共{{ nights }}晚)</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  props: {
    show: { type: Boolean, default: false }
  },
  data() {
    return {
      monthList: [],
      checkIn: '',
      checkOut: '',
      todayStr: ''
    };
  },
  computed: {
    // 计算入住晚数
    nights() {
      if (!this.checkIn || !this.checkOut) return 0;
      const t1 = new Date(this.checkIn).getTime();
      const t2 = new Date(this.checkOut).getTime();
      return Math.round((t2 - t1) / (1000 * 60 * 60 * 24));
    }
  },
  created() {
    this.initCalendar();
  },
  methods: {
    // 1. 初始化未来 4 个月的日历数据
    initCalendar() {
      const today = new Date();
      this.todayStr = this.formatDate(today);
      let year = today.getFullYear();
      let month = today.getMonth() + 1; // getMonth() 返回 0-11
      
      const list = [];
      for (let i = 0; i < 4; i++) {
        list.push(this.getMonthData(year, month));
        month++;
        if (month > 12) { month = 1; year++; }
      }
      this.monthList = list;
    },
    
    // 生成单个月份的数据
    getMonthData(year, month) {
      // 获取这个月的第一天是星期几 (0是周日)
      const firstDay = new Date(year, month - 1, 1).getDay();
      // 获取这个月有多少天
      const daysInMonth = new Date(year, month, 0).getDate();
      
      const days = [];
      const todayTime = new Date(this.todayStr).getTime();
      
      for (let i = 1; i <= daysInMonth; i++) {
        const dateStr = this.formatDate(new Date(year, month - 1, i));
        const dateTime = new Date(dateStr).getTime();
        days.push({
          day: i,
          date: dateStr,
          disabled: dateTime < todayTime // 过去的日期不可点
        });
      }
      return { year, month, emptyDays: firstDay, days };
    },
    
    // 2. 核心交互逻辑：点击日期
    selectDay(day) {
      if (day.disabled) return;
      
      // 场景 A: 还没选入住，或者已经选好了完整的入离，重新开始选
      if (!this.checkIn || (this.checkIn && this.checkOut)) {
        this.checkIn = day.date;
        this.checkOut = '';
        return;
      }
      
      // 场景 B: 已经选了入住，现在选离店
      if (this.checkIn && !this.checkOut) {
        const tIn = new Date(this.checkIn).getTime();
        const tClick = new Date(day.date).getTime();
        
        if (tClick < tIn) {
          // 如果点的日期比入住还早，就把它变成新的入住日
          this.checkIn = day.date;
        } else if (tClick === tIn) {
          // 点了同一天，无视
          return;
        } else {
          // 正常的离店日
          this.checkOut = day.date;
        }
      }
    },
    
    // 判断是否在入离区间内 (用于高亮淡蓝色)
    isInRange(date) {
      if (!this.checkIn || !this.checkOut) return false;
      const t = new Date(date).getTime();
      const t1 = new Date(this.checkIn).getTime();
      const t2 = new Date(this.checkOut).getTime();
      return t > t1 && t < t2;
    },
    
    // 点击完成按钮，把数据抛给父页面 (首页)
    confirm() {
      if (!this.checkIn || !this.checkOut) return;
      this.$emit('confirm', {
        checkIn: this.checkIn,
        checkOut: this.checkOut,
        nights: this.nights
      });
      this.close();
    },
    
    close() {
      this.$emit('close');
    },
    
    formatDate(date) {
      const y = date.getFullYear();
      const m = (date.getMonth() + 1).toString().padStart(2, '0');
      const d = date.getDate().toString().padStart(2, '0');
      return `${y}-${m}-${d}`;
    },
    isToday(date) { return date === this.todayStr; }
  }
};
</script>

<style scoped>
.calendar-popup { position: fixed; top: 0; left: 0; right: 0; bottom: 0; z-index: 999; }
.mask { position: absolute; width: 100%; height: 100%; background: rgba(0,0,0,0.5); }
.calendar-content { position: absolute; bottom: 0; left: 0; width: 100%; height: 80vh; background: #fff; border-radius: 24rpx 24rpx 0 0; display: flex; flex-direction: column; }
.header { display: flex; justify-content: center; align-items: center; height: 100rpx; position: relative; border-bottom: 1rpx solid #eee; }
.title { font-size: 32rpx; font-weight: bold; }
.close-btn { position: absolute; left: 30rpx; font-size: 40rpx; color: #999; padding: 10rpx; }
.week-bar { display: flex; background: #f8f8f8; height: 60rpx; align-items: center; }
.week-bar text { flex: 1; text-align: center; font-size: 24rpx; color: #666; }
.scroll-view { flex: 1; padding-bottom: 20rpx; }
.month-title { text-align: center; font-size: 32rpx; font-weight: bold; margin: 40rpx 0 20rpx; color: #333; }
.day-grid { display: flex; flex-wrap: wrap; }
.day-item { width: 14.28%; height: 110rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; margin-bottom: 10rpx; position: relative; }
.day-item.empty { visibility: hidden; }
.day-txt { font-size: 30rpx; font-weight: bold; color: #333; }
.desc-txt { font-size: 20rpx; margin-top: 4rpx; }

/* 状态样式 */
.disabled .day-txt { color: #ccc; text-decoration: line-through; }
.start, .end { background: #0086F6; color: #fff; border-radius: 8rpx; }
.start .day-txt, .start .desc-txt, .end .day-txt, .end .desc-txt { color: #fff; }
.in-range { background: #E1F1FF; }

.footer { padding: 20rpx 30rpx 60rpx; border-top: 1rpx solid #eee; background: #fff; }
.confirm-btn { background: #0086F6; color: #fff; border-radius: 45rpx; font-weight: bold; }
.confirm-btn.disabled { background: #ccc; }
</style>