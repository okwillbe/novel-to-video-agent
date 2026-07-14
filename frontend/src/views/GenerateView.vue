<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { generateVideo } from '@/api/task'

const router = useRouter()

// Form state
const content = ref('')
const style = ref('武侠')
const duration = ref(180)
const generating = ref(false)

// Style options
const styleOptions = [
  { value: '武侠', label: '武侠' },
  { value: '玄幻', label: '玄幻' },
  { value: '都市', label: '都市' },
  { value: '言情', label: '言情' },
  { value: '悬疑', label: '悬疑' },
  { value: '科幻', label: '科幻' },
  { value: '历史', label: '历史' },
  { value: '动漫', label: '动漫' },
]

// Duration options (in seconds)
const durationOptions = [
  { value: 60, label: '1分钟' },
  { value: 180, label: '3分钟' },
  { value: 300, label: '5分钟' },
  { value: 600, label: '10分钟' },
]

// Character images
const characterImages = ref<string[]>([])
const uploading = ref(false)

// Computed
const canGenerate = computed(() => {
  return content.value.trim().length > 50 && !generating.value
})

// Methods
async function handleGenerate() {
  if (!canGenerate.value) return

  generating.value = true
  try {
    const result = await generateVideo({
      content: content.value,
      style: style.value,
      duration: duration.value,
      options: {
        characterImages: characterImages.value,
      },
    })

    ElMessage.success('任务已创建，正在处理中...')
    router.push(`/tasks/${result.data.taskId}`)
  } catch (error: any) {
    ElMessage.error(error.message || '创建任务失败')
  } finally {
    generating.value = false
  }
}

function handleUploadSuccess(response: any) {
  if (response.url) {
    characterImages.value.push(response.url)
  }
}
</script>

<template>
  <div class="tw-max-w-4xl tw-mx-auto">
    <el-card class="tw-mb-6">
      <template #header>
        <h2 class="tw-text-xl tw-font-semibold">生成视频</h2>
      </template>

      <el-form label-position="top">
        <!-- Content -->
        <el-form-item label="小说内容 / 创意描述">
          <el-input
            v-model="content"
            type="textarea"
            :rows="10"
            placeholder="请输入小说内容或创意描述，至少50字..."
          />
          <div class="tw-text-sm tw-text-gray-500 tw-mt-1">
            已输入 {{ content.length }} 字
          </div>
        </el-form-item>

        <!-- Style & Duration -->
        <div class="tw-grid tw-grid-cols-2 tw-gap-4">
          <el-form-item label="风格类型">
            <el-select v-model="style" placeholder="选择风格">
              <el-option
                v-for="item in styleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="视频时长">
            <el-select v-model="duration" placeholder="选择时长">
              <el-option
                v-for="item in durationOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </div>

        <!-- Character Images (Optional) -->
        <el-form-item label="角色参考图（可选）">
          <el-upload
            action="/api/v1/upload"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :disabled="uploading"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="tw-text-sm tw-text-gray-500 tw-mt-1">
            上传角色参考图可以增强角色一致性
          </div>
        </el-form-item>

        <!-- Submit -->
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="generating"
            :disabled="!canGenerate"
            @click="handleGenerate"
          >
            {{ generating ? '正在创建任务...' : '开始生成' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Tips -->
    <el-alert
      title="提示"
      type="info"
      :closable="false"
      class="tw-mb-6"
    >
      <template #default>
        <p>Agent 会自动分析内容、规划流程、选择合适的 Skills 执行：</p>
        <ol class="tw-list-decimal tw-list-inside tw-mt-2 tw-space-y-1">
          <li>小说分析 → 提取角色、场景、剧情</li>
          <li>剧本生成 → 设计分镜方案</li>
          <li>图片生成 → 创建角色和场景图片</li>
          <li>一致性校验 → 确保视觉统一</li>
          <li>视频合成 → 最终输出成品</li>
        </ol>
      </template>
    </el-alert>
  </div>
</template>
