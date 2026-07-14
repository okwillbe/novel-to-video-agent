<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getCurrentUser, type UserInfo } from '@/api'

const user = ref<UserInfo | null>(null)
const showLogin = ref(false)

onMounted(async () => {
  await loadUser()
})

async function loadUser() {
  try {
    const res = await getCurrentUser()
    if (res.success) {
      user.value = res.data
    }
  } catch (e) {
    console.error('Failed to load user', e)
  }
}

function formatQuota(seconds: number | null, type: 'video' | 'voice'): string {
  if (!seconds) return '0'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  if (minutes > 60) {
    const hours = Math.floor(minutes / 60)
    return `${hours}小时${minutes % 60}分钟`
  }
  return minutes > 0 ? `${minutes}分钟` : `${secs}秒`
}
</script>

<template>
  <header class="tw-bg-white tw-shadow-sm tw-border-b tw-border-gray-200">
    <div class="tw-container tw-mx-auto tw-px-4">
      <div class="tw-flex tw-items-center tw-justify-between tw-h-16">
        <!-- Logo -->
        <router-link to="/" class="tw-flex tw-items-center tw-gap-2">
          <el-icon :size="28" color="#0ea5e9">
            <VideoCamera />
          </el-icon>
          <span class="tw-text-xl tw-font-bold tw-text-gray-800">
            Novel to Video
          </span>
        </router-link>

        <!-- Navigation -->
        <nav class="tw-flex tw-items-center tw-gap-2">
          <router-link to="/generate" class="nav-link">
            生成视频
          </router-link>
          <router-link to="/tasks" class="nav-link">
            任务列表
          </router-link>
          <router-link to="/skills" class="nav-link">
            技能库
          </router-link>
          <router-link to="/settings" class="nav-link">
            设置
          </router-link>
        </nav>

        <!-- User Info (单用户模式) -->
        <div class="tw-flex tw-items-center tw-gap-4">
          <template v-if="user">
            <el-dropdown>
              <span class="tw-flex tw-items-center tw-gap-2 tw-cursor-pointer">
                <el-avatar :size="32" class="tw-bg-primary-500">
                  {{ user.nickname?.charAt(0) || 'A' }}
                </el-avatar>
                <span class="tw-text-gray-700">{{ user.nickname }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item disabled>
                    <div class="tw-text-xs tw-text-gray-500">
                      <div>视频配额: {{ formatQuota(user.quotaVideoSeconds, 'video') }}</div>
                      <div>图片配额: {{ user.quotaImageCount }} 张</div>
                    </div>
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="$router.push('/settings')">
                    设置
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="showLogin = true">
              登录
            </el-button>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.nav-link {
  @apply tw-px-3 tw-py-2 tw-rounded-lg tw-text-gray-600 tw-transition-colors;
}
.nav-link:hover {
  @apply tw-bg-gray-100 tw-text-primary-600;
}
.router-link-active.nav-link {
  @apply tw-bg-primary-50 tw-text-primary-600;
}
</style>
