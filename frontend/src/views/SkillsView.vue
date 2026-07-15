<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSkills, type SkillResponse } from '@/api'

const skills = ref<SkillResponse[]>([])
const categories = ref<string[]>(['全部', 'analysis', 'generation', 'synthesis', 'postprocess'])
const activeCategory = ref('全部')
const searchKeyword = ref('')
const loading = ref(false)

onMounted(() => {
  loadSkills()
})

async function loadSkills() {
  loading.value = true
  try {
    const category = activeCategory.value === '全部' ? undefined : activeCategory.value
    const result = await getSkills(category, searchKeyword.value)
    skills.value = result.data
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function handleCategoryChange(category: string) {
  activeCategory.value = category
  loadSkills()
}

function handleSearch() {
  loadSkills()
}

function getCategoryLabel(category: string) {
  switch (category) {
    case 'analysis': return '分析'
    case 'generation': return '生成'
    case 'synthesis': return '合成'
    case 'postprocess': return '后处理'
    default: return category
  }
}

function getCategoryType(category: string) {
  switch (category) {
    case 'analysis': return 'primary'
    case 'generation': return 'success'
    case 'synthesis': return 'warning'
    case 'postprocess': return 'info'
    default: return ''
  }
}
</script>

<template>
  <div>
    <h2 class="tw-text-2xl tw-font-bold tw-mb-6">技能库</h2>

    <!-- Filters -->
    <div class="tw-flex tw-items-center tw-gap-4 tw-mb-6">
      <el-radio-group v-model="activeCategory" @change="handleCategoryChange">
        <el-radio-button
          v-for="cat in categories"
          :key="cat"
          :value="cat"
        >
          {{ cat === '全部' ? '全部' : getCategoryLabel(cat) }}
        </el-radio-button>
      </el-radio-group>

      <el-input
        v-model="searchKeyword"
        placeholder="搜索技能..."
        class="tw-w-64"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- Skills Grid -->
    <div v-loading="loading" class="tw-grid tw-grid-cols-1 md:tw-grid-cols-2 lg:tw-grid-cols-3 tw-gap-4">
      <el-card
        v-for="skill in skills"
        :key="skill.skillId"
        shadow="hover"
        class="tw-cursor-pointer"
      >
        <div class="tw-flex tw-items-start tw-justify-between tw-mb-3">
          <div>
            <h3 class="tw-font-semibold tw-text-lg">{{ skill.name }}</h3>
            <el-tag :type="getCategoryType(skill.category)" size="small" class="tw-mt-1">
              {{ getCategoryLabel(skill.category) }}
            </el-tag>
          </div>
          <el-tag size="small" type="info">v{{ skill.version }}</el-tag>
        </div>

        <p class="tw-text-gray-600 tw-text-sm tw-mb-4">{{ skill.description }}</p>

        <div class="tw-flex tw-items-center tw-justify-between tw-text-sm tw-text-gray-500">
          <div class="tw-flex tw-items-center tw-gap-1">
            <el-icon><Star /></el-icon>
            <span>{{ skill.avgRating?.toFixed(1) || '-' }}</span>
          </div>
          <div class="tw-flex tw-items-center tw-gap-1">
            <el-icon><DataLine /></el-icon>
            <span>{{ skill.useCount }}次使用</span>
          </div>
        </div>

        <div class="tw-flex tw-flex-wrap tw-gap-1 tw-mt-3">
          <el-tag
            v-for="tag in skill.tags"
            :key="tag"
            size="small"
            effect="plain"
          >
            {{ tag }}
          </el-tag>
        </div>
      </el-card>
    </div>
  </div>
</template>
