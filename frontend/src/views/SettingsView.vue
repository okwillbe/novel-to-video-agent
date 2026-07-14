<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// Settings state
const settings = ref({
  llmProvider: 'google',
  llmModel: 'gemini-2.5-flash',
  imageProvider: 'fal',
  imageModel: 'flux-schnell',
  videoProvider: 'kling',
  videoModel: 'kling-video-v1',
  voiceProvider: 'elevenlabs',
  voiceModel: 'eleven-multilingual-v2',
})

// API Keys (in real app, these would be encrypted)
const apiKeys = ref({
  googleApiKey: '',
  falApiKey: '',
  klingApiKey: '',
  elevenlabsApiKey: '',
})

const saving = ref(false)

// Provider options
const llmProviders = [
  { value: 'google', label: 'Google AI' },
  { value: 'openai', label: 'OpenAI' },
  { value: 'anthropic', label: 'Anthropic' },
  { value: 'deepseek', label: 'DeepSeek' },
]

const llmModels: Record<string, { value: string; label: string }[]> = {
  google: [
    { value: 'gemini-2.5-pro', label: 'Gemini 2.5 Pro' },
    { value: 'gemini-2.5-flash', label: 'Gemini 2.5 Flash' },
  ],
  openai: [
    { value: 'gpt-4o', label: 'GPT-4o' },
    { value: 'gpt-4o-mini', label: 'GPT-4o Mini' },
  ],
  anthropic: [
    { value: 'claude-sonnet-4-6', label: 'Claude Sonnet 4.6' },
    { value: 'claude-haiku-4-5', label: 'Claude Haiku 4.5' },
  ],
  deepseek: [
    { value: 'deepseek-chat', label: 'DeepSeek Chat' },
    { value: 'deepseek-reasoner', label: 'DeepSeek Reasoner' },
  ],
}

const imageProviders = [
  { value: 'fal', label: 'FAL.AI' },
  { value: 'kling', label: 'Kling' },
  { value: 'stability', label: 'Stability AI' },
]

const videoProviders = [
  { value: 'kling', label: 'Kling' },
  { value: 'runway', label: 'Runway' },
]

const voiceProviders = [
  { value: 'elevenlabs', label: 'ElevenLabs' },
  { value: 'fish-speech', label: 'Fish Speech' },
]

async function handleSave() {
  saving.value = true
  try {
    // In real app, would call API to save settings
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('设置已保存')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="tw-max-w-4xl tw-mx-auto">
    <h2 class="tw-text-2xl tw-font-bold tw-mb-6">设置</h2>

    <!-- API Keys -->
    <el-card class="tw-mb-6">
      <template #header>
        <span class="tw-font-semibold">API 密钥配置</span>
      </template>

      <el-form label-position="top">
        <el-form-item label="Google AI API Key">
          <el-input
            v-model="apiKeys.googleApiKey"
            type="password"
            placeholder="输入你的 Google AI API Key"
            show-password
          />
        </el-form-item>

        <el-form-item label="FAL.AI API Key">
          <el-input
            v-model="apiKeys.falApiKey"
            type="password"
            placeholder="输入你的 FAL.AI API Key"
            show-password
          />
        </el-form-item>

        <el-form-item label="Kling API Key">
          <el-input
            v-model="apiKeys.klingApiKey"
            type="password"
            placeholder="输入你的 Kling API Key"
            show-password
          />
        </el-form-item>

        <el-form-item label="ElevenLabs API Key">
          <el-input
            v-model="apiKeys.elevenlabsApiKey"
            type="password"
            placeholder="输入你的 ElevenLabs API Key"
            show-password
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Default Providers -->
    <el-card class="tw-mb-6">
      <template #header>
        <span class="tw-font-semibold">默认服务配置</span>
      </template>

      <el-form label-position="top">
        <div class="tw-grid tw-grid-cols-2 tw-gap-4">
          <!-- LLM -->
          <el-form-item label="LLM 服务商">
            <el-select v-model="settings.llmProvider">
              <el-option
                v-for="p in llmProviders"
                :key="p.value"
                :label="p.label"
                :value="p.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="LLM 模型">
            <el-select v-model="settings.llmModel">
              <el-option
                v-for="m in llmModels[settings.llmProvider] || []"
                :key="m.value"
                :label="m.label"
                :value="m.value"
              />
            </el-select>
          </el-form-item>

          <!-- Image -->
          <el-form-item label="图片生成服务商">
            <el-select v-model="settings.imageProvider">
              <el-option
                v-for="p in imageProviders"
                :key="p.value"
                :label="p.label"
                :value="p.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="图片模型">
            <el-input v-model="settings.imageModel" />
          </el-form-item>

          <!-- Video -->
          <el-form-item label="视频生成服务商">
            <el-select v-model="settings.videoProvider">
              <el-option
                v-for="p in videoProviders"
                :key="p.value"
                :label="p.label"
                :value="p.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="视频模型">
            <el-input v-model="settings.videoModel" />
          </el-form-item>

          <!-- Voice -->
          <el-form-item label="配音服务商">
            <el-select v-model="settings.voiceProvider">
              <el-option
                v-for="p in voiceProviders"
                :key="p.value"
                :label="p.label"
                :value="p.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="配音模型">
            <el-input v-model="settings.voiceModel" />
          </el-form-item>
        </div>
      </el-form>
    </el-card>

    <!-- Save Button -->
    <div class="tw-flex tw-justify-end">
      <el-button type="primary" size="large" :loading="saving" @click="handleSave">
        保存设置
      </el-button>
    </div>
  </div>
</template>
