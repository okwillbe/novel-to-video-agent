<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTasks, type TaskSummary } from '@/api/task'

const router = useRouter()

const tasks = ref<TaskSummary[]>([])
const loading = ref(false)

onMounted(() => {
  loadTasks()
})

async function loadTasks() {
  loading.value = true
  try {
    const result = await getTasks(20)
    tasks.value = result.data
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function getStatusType(status: string) {
  switch (status) {
    case 'completed':
      return 'success'
    case 'failed':
    case 'cancelled':
      return 'danger'
    case 'processing':
      return 'primary'
    default:
      return 'info'
  }
}

function getStatusLabel(status: string) {
  switch (status) {
    case 'pending':
      return '等待中'
    case 'processing':
      return '处理中'
    case 'completed':
      return '已完成'
    case 'failed':
      return '失败'
    case 'cancelled':
      return '已取消'
    default:
      return status
  }
}

function goToDetail(task: TaskSummary) {
  router.push(`/tasks/${task.taskId}`)
}
</script>

<template>
  <div>
    <div class="tw-flex tw-items-center tw-justify-between tw-mb-6">
      <h2 class="tw-text-2xl tw-font-bold">任务列表</h2>
      <el-button type="primary" @click="router.push('/generate')">
        新建任务
      </el-button>
    </div>

    <el-card v-loading="loading">
      <el-table :data="tasks" @row-click="goToDetail">
        <el-table-column prop="taskId" label="任务ID" width="200">
          <template #default="{ row }">
            <span class="tw-font-mono tw-text-sm">{{ row.taskId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="taskType" label="类型" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="row.progress"
              :status="row.status === 'completed' ? 'success' : undefined"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.createdAt).toLocaleString('zh-CN') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
