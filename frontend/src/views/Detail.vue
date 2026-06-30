<template>
    <div class="page-container">
        <div class="page-header">
            <van-nav-bar fixed left-text="返回" left-arrow @click-left="onBack" :title="formData.city + '行程规划'" />
        </div>
        <div class="page-content">
            <div v-if="isLoading" class="loading-container">
                <van-loading size="48ox" type="spinner">
                    正在生成旅游规划...
                </van-loading>
            </div>
            <div v-else-if="errorMsg">
                <van-empty :description="errorMsg">
                    <van-button type="primary" @click="fetchTripData">重试</van-button>
                </van-empty>
            </div>
            <template v-else-if="tripData && tripData.successs !== false">
                <div class="card overview-card">
                    <div class="trip-header">
                        <h2>{{ tripData.city }} · {{ tripData.days }}天行程</h2>
                        <div class="trip-budget">预算：{{ tripData.totalBudget }}元</div>
                    </div>
                </div>
                <van-collapse v-model="activeDays" class="trip-collapse">
                    <van-collapse-item 
                        v-for="day in tripData.dailyItinerary" 
                        :key="day.days" 
                        :title="'第' + day.day + '天'"
                        :name="day.days">
                        <div class="day-schedule">
                            <div class="schedule-section">
                                <div class="section-label morning">上午</div>
                                <SpotItem :data="day.morning" />
                            </div>
                            <div class="schedule-section">
                                <div class="section-label afternoon">下午</div>
                                <SpotItem :data="day.afternoon" />
                            </div>
                            <div class="schedule-section">
                                <div class="section-label evening">晚上</div>
                                <SpotItem :data="day.evening" />
                            </div>
                        </div>
                    </van-collapse-item>
                </van-collapse>
                <div class="card budget-card" v-if="tripData.budgetBreakdown">
                    <div class="section-title">
                        预算明细
                    </div>
                    <BudgetTable :data="tripData.budgetBreakdown" :total="tripData.totalBudget" />
                </div>
                <div class="card tips-card" v-if="tripData.tips && tripData.tips.length">
                    <div class="section-title">
                        温馨提示
                    </div>
                    <ul class="tips-list">
                        <li v-for="(tip, index) in tripData.tips" :key="index">{{ tip }}</li>
                    </ul>
                </div>
                <div class="card warnings-card" v-if="tripData.warnings && tripData.warnings.length">
                    <div class="section-title">
                        注意事项
                    </div>
                    <ul class="warnings-list">
                        <li v-for="(warning, index) in tripData.warnings" :key="index">{{ warning }}</li>
                    </ul>
                </div>
            </template>
        </div>
        <div class="detail-footer" v-if="tripData && tripData.successs !== false">
            <van-button type="primary" size="large" round @click="goToChat" class="primary-button">
                咨询 AI 助手
            </van-button>
        </div>
    </div>
</template>
<script setup>

import { onMounted, reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { post } from '../utils/request'
import SpotItem from '../components/SpotItem.vue'
import BudgetTable from '../components/BudgetTable.vue'

const router = useRouter()
const route = useRoute()

// 加载状态
const isLoading = ref(true)

// 表单数据
const formData = reactive({
    city: '',
    budget: null,
    days: null
})

// 活动天数
const activeDays = ref([])

const tripData = ref(null)

const errorMsg = ref('')

const onBack = () => {
    router.back()
}

const goToChat = () => {
    router.push({
        path: '/chat',
        query: {
            scene: 'detail',
            city: formData.city
        }
    })
}

// 行程规划数据
const fetchTripData = async () => {
    const res = await post('recommend', {
        city: formData.city,
        budget: formData.budget,
        days: formData.days
    })
    isLoading.value = false
    if (res && res.successs != false) {
        tripData.value = res
    } else {
        errorMsg.value = res.error
    }
}

onMounted(() => {
    formData.city = route.query.city
    formData.budget = route.query.budget
    formData.days = route.query.days

    if (formData.city && formData.budget && formData.days) {
        fetchTripData()
    }
})
</script>
<style scoped>
.page-header {
   height: 46px;
}
.overview-card {
    margin-bottom: 16px;
}

.trip-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.trip-header h2 {
    font-size: 20px;
    color: #323233;
    margin: 0;
}

.trip-budget {
    font-size: 16px;
    color: #ee0a24;
    font-weight: 600;
}

.trip-collapse {
    margin-bottom: 16px;
}

.day-schedule {
    padding: 8px 0;
}

.schedule-section {
    margin-bottom: 16px;
}

.schedule-section:last-child {
    margin-bottom: 0;
}

.section-label {
    font-size: 14px;
    font-weight: 600;
    padding: 4px 8px;
    border-radius: 4px;
    display: inline-block;
    margin-bottom: 8px;
}

.section-label.morning {
    background: #fff7e6;
    color: #fa8c16;
}

.section-label.afternoon {
    background: #e6f7ff;
    color: #1890ff;
}

.section-label.evening {
    background: #f6ffed;
    color: #52c41a;
}

.budget-card,
.tips-card,
.warnings-card {
    margin-bottom: 16px;
}

.tips-list,
.warnings-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.tips-list li,
.warnings-list li {
    padding: 8px 0;
    color: #666;
    font-size: 14px;
    border-bottom: 1px solid #f5f5f5;
}

.tips-list li:last-child,
.warnings-list li:last-child {
    border-bottom: none;
}

.detail-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 12px 16px;
    background: #fff;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
    max-width: 750px;
    margin: 0 auto;
}

.error-card {
    text-align: center;
    padding: 40px 16px;
}
</style>
