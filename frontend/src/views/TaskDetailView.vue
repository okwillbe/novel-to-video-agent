<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTaskStatus, cancelTask, type TaskStatusResponse, type StepInfo } from '@/api/task'

const route = useRoute()
const taskId = route.params.id as string

const task = ref<TaskStatusResponse | null>(null)
const loading = ref(false)
const polling = ref<number | null>(null)

onMounted(() => {
  loadTask()
  // Poll for updates if task is processing
  polling.value = window.setInterval(() => {
    if (task.value && task.value.status === 'processing') {
      loadTask()
    }
  }, 3000)
})

onUnmounted(() => {
  if (polling.value) {
    clearInterval(polling.value)
  }
})

async function loadTask() {
  try {
    const result = await getTaskStatus(taskId)
    task.value = result.data
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  }
}

async function handleCancel() {
  try {
    await cancelTask(taskId)
    ElMessage.success('任务已取消')
    loadTask()
  } catch (error: any) {
    ElMessage.error(error.message || '取消失败')
  }
}

function getStepStatus(step: StepInfo) {
  if (step.status === 'completed') return 'success'
  if (step.status === 'failed') return 'error'
  if (step.status === 'running') return 'process'
  return 'wait'
}

function getStatusLabel(status: string) {
  switch (status) {
    case 'pending': return '等待中'
    case 'processing': return '处理中'
    case 'completed': return '已完成'
    case 'failed': return '失败'
    case 'cancelled': return '已取消'
    default: return status
  }
}

function getStatusType(status: string) {
  switch (status) {
    case 'completed': return 'success'
    case 'failed':
    case 'cancelled': return 'danger'
    case 'processing': return 'primary'
    default: return 'info'
  }
}
</script>

<template>
  <div v-loading="loading && !task">
    <div class="tw-flex tw-items-center tw-justify-between tw-mb-6">
      <div>
        <h2 class="tw-text-2xl tw-font-bold">任务详情</h2>
        <p class="tw-text-gray-500 tw-font-mono tw-text-sm tw-mt-1">
          {{ taskId }}
        </p>
      </div>
      <div class="tw-flex tw-gap-2">
        <el-tag v-if="task" :type="getStatusType(task.status)" size="large">
          {{ getStatusLabel(task.status) }}
        </el-tag>
        <el-button
          v-if="task && (task.status === 'pending' || task.status === 'processing')"
          type="danger"
          @click="handleCancel"
        >
          取消任务
        </el-button>
      </div>
    </div>

    <!-- Progress -->
    <el-card v-if="task" class="tw-mb-6">
      <template #header>
        <span class="tw-font-semibold">整体进度</span>
      </template>
      <el-progress
        :percentage="task.progress"
        :status="task.status === 'completed' ? 'success' : undefined"
        :stroke-width="20"
      />
      <p v-if="task.currentStep" class="tw-mt-4 tw-text-gray-600">
        当前步骤: {{ task.currentStep }}
      </p>
    </el-card>

    <!-- Steps -->
    <el-card v-if="task && task.steps.length > 0">
      <template #header>
        <span class="tw-font-semibold">执行步骤</span>
      </template>
      <el-steps :active="task.steps.findIndex(s => s.status === 'running') + 1" align-center>
        <el-step
          v-for="(step, index) in task.steps"
          :key="index"
          :title="step.name"
          :status="getStepStatus(step)"
        >
          <template #description>
            <div class="tw-text-center">
              <el-progress
                v-if="step.status === 'running'"
                :percentage="step.progress"
                :stroke-width="10"
              />
            </div>
          </template>
        </el-step>
      </el-steps>
    </el-card>
  </div>
</template>
