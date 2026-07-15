<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCurrentUser, type UserInfo } from '@/api'

const router = useRouter()
const user = ref<UserInfo | null>(null)

onMounted(async () => {
  try {
    const res = await getCurrentUser()
    if (res.success) {
      user.value = res.data
    }
  } catch (e) {
    console.error('Failed to load user', e)
  }
})

const features = [
  {
    title: '智能分析',
    description: '自动解析小说，提取角色、场景、剧情',
    icon: 'Document',
  },
  {
    title: '角色生成',
    description: 'AI生成一致性角色图片',
    icon: 'User',
  },
  {
    title: '分镜设计',
    description: '专业分镜方案，自动生成镜头',
    icon: 'Film',
  },
  {
    title: '视频合成',
    description: '一键合成完整视频，支持配音',
    icon: 'VideoCamera',
  },
]

function goToGenerate() {
  router.push('/generate')
}
</script>

<template>
  <div>
    <!-- Hero Section -->
    <section class="tw-py-16 tw-text-center">
      <h1 class="tw-text-4xl tw-font-bold tw-text-gray-900 tw-mb-4">
        智能小说转视频 Agent
      </h1>
      <p class="tw-text-lg tw-text-gray-600 tw-mb-8 tw-max-w-2xl tw-mx-auto">
        只需描述需求，Agent 自动规划执行。基于 agentscope-java 的 ReAct 推理引擎。
      </p>

      <!-- 配额显示 -->
      <div v-if="user" class="tw-mb-8 tw-flex tw-justify-center tw-gap-8">
        <div class="tw-text-center">
          <div class="tw-text-2xl tw-font-bold tw-text-primary-600">
            {{ Math.floor((user.quotaVideoSeconds || 0) / 60) }}
          </div>
          <div class="tw-text-sm tw-text-gray-500">视频分钟</div>
        </div>
        <div class="tw-text-center">
          <div class="tw-text-2xl tw-font-bold tw-text-primary-600">
            {{ user.quotaImageCount || 0 }}
          </div>
          <div class="tw-text-sm tw-text-gray-500">图片配额</div>
        </div>
        <div class="tw-text-center">
          <div class="tw-text-2xl tw-font-bold tw-text-primary-600">
            {{ Math.floor((user.quotaTextTokens || 0) / 10000) }}
          </div>
          <div class="tw-text-sm tw-text-gray-500">Token(万)</div>
        </div>
      </div>

      <el-button type="primary" size="large" @click="goToGenerate">
        开始生成视频
      </el-button>
    </section>

    <!-- Features -->
    <section class="tw-grid tw-grid-cols-1 md:tw-grid-cols-2 lg:tw-grid-cols-4 tw-gap-6 tw-mt-8">
      <el-card
        v-for="feature in features"
        :key="feature.title"
        class="tw-text-center tw-cursor-pointer hover:tw-shadow-lg tw-transition-shadow"
        shadow="hover"
      >
        <el-icon :size="48" color="#0ea5e9" class="tw-mb-4">
          <component :is="feature.icon" />
        </el-icon>
        <h3 class="tw-text-lg tw-font-semibold tw-mb-2">{{ feature.title }}</h3>
        <p class="tw-text-gray-600 tw-text-sm">{{ feature.description }}</p>
      </el-card>
    </section>

    <!-- 单用户模式提示 -->
    <el-alert
      type="info"
      :closable="false"
      class="tw-mt-8"
    >
      <template #title>
        <span class="tw-font-semibold">单用户模式</span>
      </template>
      当前为单用户版本，默认账号: admin / admin
    </el-alert>
  </div>
</template>
