<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  FolderOpened,
  User,
  Location,
  Microphone,
  Plus,
  Edit,
  Delete,
  Picture,
} from '@element-plus/icons-vue'
import {
  getFolders, createFolder, deleteFolder,
  getCharacters, getCharacter, createCharacter, updateCharacter, deleteCharacter,
  createAppearance, deleteAppearance,
  getLocations, getLocation, createLocation, updateLocation, deleteLocation,
  createLocationImage, deleteLocationImage,
  getVoices, getVoice, createVoice, updateVoice, deleteVoice,
  type Folder, type Character, type Appearance, type Location, type LocationImage, type Voice,
} from '@/api/assets'

// ==================== 状态 ====================

const loading = ref(false)
const activeTab = ref('characters')

// 文件夹
const folders = ref<Folder[]>([])
const selectedFolderId = ref<string | null>(null)
const showFolderDialog = ref(false)
const editingFolder = ref<Folder | null>(null)
const folderForm = ref({ name: '', sortOrder: 0 })

// 角色
const characters = ref<Character[]>([])
const showCharacterDialog = ref(false)
const editingCharacter = ref<Character | null>(null)
const characterForm = ref({
  name: '',
  folderId: null as string | null,
  aliases: [] as string[],
  profileConfirmed: false,
})

// 场景
const locations = ref<Location[]>([])
const showLocationDialog = ref(false)
const editingLocation = ref<Location | null>(null)
const locationForm = ref({
  name: '',
  folderId: null as string | null,
  summary: '',
  artStyle: '',
})

// 音色
const voices = ref<Voice[]>([])
const showVoiceDialog = ref(false)
const editingVoice = ref<Voice | null>(null)
const voiceForm = ref({
  name: '',
  folderId: null as string | null,
  description: '',
  gender: '',
  language: 'zh',
})

// ==================== 初始化 ====================

onMounted(async () => {
  await loadFolders()
  await loadAllAssets()
})

async function loadFolders() {
  try {
    const res = await getFolders()
    if (res.success) {
      folders.value = res.data
    }
  } catch (e: any) {
    ElMessage.error(e.message || '加载文件夹失败')
  }
}

async function loadAllAssets() {
  loading.value = true
  try {
    const [charRes, locRes, voiceRes] = await Promise.all([
      getCharacters(),
      getLocations(),
      getVoices(),
    ])
    if (charRes.success) characters.value = charRes.data
    if (locRes.success) locations.value = locRes.data
    if (voiceRes.success) voices.value = voiceRes.data
  } catch (e: any) {
    ElMessage.error(e.message || '加载资产失败')
  } finally {
    loading.value = false
  }
}

// ==================== 文件夹操作 ====================

function selectFolder(folderId: string | null) {
  selectedFolderId.value = folderId
}

function openCreateFolderDialog() {
  editingFolder.value = null
  folderForm.value = { name: '', sortOrder: 0 }
  showFolderDialog.value = true
}

async function saveFolder() {
  if (!folderForm.value.name.trim()) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  try {
    await createFolder(folderForm.value)
    ElMessage.success('创建成功')
    showFolderDialog.value = false
    await loadFolders()
  } catch (e: any) {
    ElMessage.error(e.message || '创建失败')
  }
}

async function removeFolder(folder: Folder) {
  try {
    await ElMessageBox.confirm(`确定删除文件夹 "${folder.name}" 吗？`, '确认删除', {
      type: 'warning',
    })
    await deleteFolder(folder.folderId)
    ElMessage.success('删除成功')
    if (selectedFolderId.value === folder.folderId) {
      selectedFolderId.value = null
    }
    await loadFolders()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

// ==================== 角色操作 ====================

const filteredCharacters = computed(() => {
  if (!selectedFolderId.value) return characters.value
  return characters.value.filter(c => c.folderId === selectedFolderId.value)
})

function openCreateCharacterDialog() {
  editingCharacter.value = null
  characterForm.value = {
    name: '',
    folderId: selectedFolderId.value,
    aliases: [],
    profileConfirmed: false,
  }
  showCharacterDialog.value = true
}

async function saveCharacter() {
  if (!characterForm.value.name.trim()) {
    ElMessage.warning('请输入角色名称')
    return
  }
  try {
    if (editingCharacter.value) {
      await updateCharacter(editingCharacter.value.characterId, characterForm.value)
      ElMessage.success('更新成功')
    } else {
      await createCharacter(characterForm.value)
      ElMessage.success('创建成功')
    }
    showCharacterDialog.value = false
    await loadAllAssets()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function removeCharacter(character: Character) {
  try {
    await ElMessageBox.confirm(`确定删除角色 "${character.name}" 吗？`, '确认删除', {
      type: 'warning',
    })
    await deleteCharacter(character.characterId)
    ElMessage.success('删除成功')
    await loadAllAssets()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

// ==================== 场景操作 ====================

const filteredLocations = computed(() => {
  if (!selectedFolderId.value) return locations.value
  return locations.value.filter(l => l.folderId === selectedFolderId.value)
})

function openCreateLocationDialog() {
  editingLocation.value = null
  locationForm.value = {
    name: '',
    folderId: selectedFolderId.value,
    summary: '',
    artStyle: '',
  }
  showLocationDialog.value = true
}

async function saveLocation() {
  if (!locationForm.value.name.trim()) {
    ElMessage.warning('请输入场景名称')
    return
  }
  try {
    if (editingLocation.value) {
      await updateLocation(editingLocation.value.locationId, locationForm.value)
      ElMessage.success('更新成功')
    } else {
      await createLocation(locationForm.value)
      ElMessage.success('创建成功')
    }
    showLocationDialog.value = false
    await loadAllAssets()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function removeLocation(location: Location) {
  try {
    await ElMessageBox.confirm(`确定删除场景 "${location.name}" 吗？`, '确认删除', {
      type: 'warning',
    })
    await deleteLocation(location.locationId)
    ElMessage.success('删除成功')
    await loadAllAssets()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

// ==================== 音色操作 ====================

const filteredVoices = computed(() => {
  if (!selectedFolderId.value) return voices.value
  return voices.value.filter(v => v.folderId === selectedFolderId.value)
})

function openCreateVoiceDialog() {
  editingVoice.value = null
  voiceForm.value = {
    name: '',
    folderId: selectedFolderId.value,
    description: '',
    gender: '',
    language: 'zh',
  }
  showVoiceDialog.value = true
}

async function saveVoice() {
  if (!voiceForm.value.name.trim()) {
    ElMessage.warning('请输入音色名称')
    return
  }
  try {
    if (editingVoice.value) {
      await updateVoice(editingVoice.value.voiceId, voiceForm.value)
      ElMessage.success('更新成功')
    } else {
      await createVoice(voiceForm.value)
      ElMessage.success('创建成功')
    }
    showVoiceDialog.value = false
    await loadAllAssets()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function removeVoice(voice: Voice) {
  try {
    await ElMessageBox.confirm(`确定删除音色 "${voice.name}" 吗？`, '确认删除', {
      type: 'warning',
    })
    await deleteVoice(voice.voiceId)
    ElMessage.success('删除成功')
    await loadAllAssets()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}
</script>

<template>
  <div class="tw-flex tw-gap-6 tw-h-full">
    <!-- 左侧：文件夹树 -->
    <aside class="tw-w-64 tw-flex-shrink-0">
      <el-card class="tw-h-full">
        <template #header>
          <div class="tw-flex tw-items-center tw-justify-between">
            <span class="tw-font-semibold">文件夹</span>
            <el-button type="primary" :icon="Plus" size="small" @click="openCreateFolderDialog" />
          </div>
        </template>

        <div class="tw-space-y-1">
          <!-- 全部 -->
          <div
            class="folder-item"
            :class="{ active: selectedFolderId === null }"
            @click="selectFolder(null)"
          >
            <el-icon><FolderOpened /></el-icon>
            <span>全部资产</span>
          </div>

          <!-- 文件夹列表 -->
          <div
            v-for="folder in folders"
            :key="folder.folderId"
            class="folder-item"
            :class="{ active: selectedFolderId === folder.folderId }"
            @click="selectFolder(folder.folderId)"
          >
            <el-icon><FolderOpened /></el-icon>
            <span class="tw-flex-1 tw-truncate">{{ folder.name }}</span>
            <el-button
              type="danger"
              :icon="Delete"
              size="small"
              link
              @click.stop="removeFolder(folder)"
            />
          </div>
        </div>
      </el-card>
    </aside>

    <!-- 右侧：资产列表 -->
    <main class="tw-flex-1 tw-overflow-auto">
      <el-tabs v-model="activeTab">
        <!-- 角色列表 -->
        <el-tab-pane label="角色" name="characters">
          <div class="tw-flex tw-justify-between tw-items-center tw-mb-4">
            <span class="tw-text-lg tw-font-semibold">角色列表</span>
            <el-button type="primary" :icon="Plus" @click="openCreateCharacterDialog">新建角色</el-button>
          </div>
          <div class="tw-grid tw-grid-cols-1 md:tw-grid-cols-2 lg:tw-grid-cols-3 tw-gap-4">
            <el-card
              v-for="character in filteredCharacters"
              :key="character.characterId"
              class="asset-card"
              shadow="hover"
            >
              <div class="tw-flex tw-items-start tw-gap-3">
                <el-avatar :size="48" class="tw-bg-primary-500">
                  {{ character.name?.charAt(0) }}
                </el-avatar>
                <div class="tw-flex-1 tw-min-w-0">
                  <h3 class="tw-font-semibold tw-truncate">{{ character.name }}</h3>
                  <p class="tw-text-sm tw-text-gray-500 tw-mt-1">
                    {{ character.appearances?.length || 0 }} 个形象
                  </p>
                </div>
                <el-dropdown trigger="click">
                  <el-button :icon="Edit" link />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="editingCharacter = character; showCharacterDialog = true">
                        编辑
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="removeCharacter(character)">
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </el-card>
            <el-empty v-if="filteredCharacters.length === 0" description="暂无角色" />
          </div>
        </el-tab-pane>

        <!-- 场景列表 -->
        <el-tab-pane label="场景" name="locations">
          <div class="tw-flex tw-justify-between tw-items-center tw-mb-4">
            <span class="tw-text-lg tw-font-semibold">场景列表</span>
            <el-button type="primary" :icon="Plus" @click="openCreateLocationDialog">新建场景</el-button>
          </div>
          <div class="tw-grid tw-grid-cols-1 md:tw-grid-cols-2 lg:tw-grid-cols-3 tw-gap-4">
            <el-card
              v-for="location in filteredLocations"
              :key="location.locationId"
              class="asset-card"
              shadow="hover"
            >
              <div class="tw-flex tw-items-start tw-gap-3">
                <el-avatar :size="48" class="tw-bg-green-500">
                  <el-icon><Location /></el-icon>
                </el-avatar>
                <div class="tw-flex-1 tw-min-w-0">
                  <h3 class="tw-font-semibold tw-truncate">{{ location.name }}</h3>
                  <p class="tw-text-sm tw-text-gray-500 tw-mt-1 tw-truncate">
                    {{ location.summary || '暂无描述' }}
                  </p>
                </div>
                <el-dropdown trigger="click">
                  <el-button :icon="Edit" link />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="editingLocation = location; showLocationDialog = true">
                        编辑
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="removeLocation(location)">
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </el-card>
            <el-empty v-if="filteredLocations.length === 0" description="暂无场景" />
          </div>
        </el-tab-pane>

        <!-- 音色列表 -->
        <el-tab-pane label="音色" name="voices">
          <div class="tw-flex tw-justify-between tw-items-center tw-mb-4">
            <span class="tw-text-lg tw-font-semibold">音色列表</span>
            <el-button type="primary" :icon="Plus" @click="openCreateVoiceDialog">新建音色</el-button>
          </div>
          <div class="tw-grid tw-grid-cols-1 md:tw-grid-cols-2 lg:tw-grid-cols-3 tw-gap-4">
            <el-card
              v-for="voice in filteredVoices"
              :key="voice.voiceId"
              class="asset-card"
              shadow="hover"
            >
              <div class="tw-flex tw-items-start tw-gap-3">
                <el-avatar :size="48" class="tw-bg-purple-500">
                  <el-icon><Microphone /></el-icon>
                </el-avatar>
                <div class="tw-flex-1 tw-min-w-0">
                  <h3 class="tw-font-semibold tw-truncate">{{ voice.name }}</h3>
                  <p class="tw-text-sm tw-text-gray-500 tw-mt-1">
                    {{ voice.gender || '未知' }} · {{ voice.language }}
                  </p>
                </div>
                <el-dropdown trigger="click">
                  <el-button :icon="Edit" link />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="editingVoice = voice; showVoiceDialog = true">
                        编辑
                      </el-dropdown-item>
                      <el-dropdown-item divided @click="removeVoice(voice)">
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </el-card>
            <el-empty v-if="filteredVoices.length === 0" description="暂无音色" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </main>

    <!-- 创建文件夹对话框 -->
    <el-dialog v-model="showFolderDialog" title="新建文件夹" width="400px">
      <el-form label-position="top">
        <el-form-item label="文件夹名称">
          <el-input v-model="folderForm.name" placeholder="请输入文件夹名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFolderDialog = false">取消</el-button>
        <el-button type="primary" @click="saveFolder">确定</el-button>
      </template>
    </el-dialog>

    <!-- 创建角色对话框 -->
    <el-dialog v-model="showCharacterDialog" title="新建角色" width="500px">
      <el-form label-position="top">
        <el-form-item label="角色名称">
          <el-input v-model="characterForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="归属文件夹">
          <el-select v-model="characterForm.folderId" clearable placeholder="选择文件夹">
            <el-option
              v-for="folder in folders"
              :key="folder.folderId"
              :label="folder.name"
              :value="folder.folderId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCharacterDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCharacter">确定</el-button>
      </template>
    </el-dialog>

    <!-- 创建场景对话框 -->
    <el-dialog v-model="showLocationDialog" title="新建场景" width="500px">
      <el-form label-position="top">
        <el-form-item label="场景名称">
          <el-input v-model="locationForm.name" placeholder="请输入场景名称" />
        </el-form-item>
        <el-form-item label="场景简介">
          <el-input v-model="locationForm.summary" type="textarea" :rows="3" placeholder="请输入场景简介" />
        </el-form-item>
        <el-form-item label="艺术风格">
          <el-input v-model="locationForm.artStyle" placeholder="如：水墨风、写实风" />
        </el-form-item>
        <el-form-item label="归属文件夹">
          <el-select v-model="locationForm.folderId" clearable placeholder="选择文件夹">
            <el-option
              v-for="folder in folders"
              :key="folder.folderId"
              :label="folder.name"
              :value="folder.folderId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLocationDialog = false">取消</el-button>
        <el-button type="primary" @click="saveLocation">确定</el-button>
      </template>
    </el-dialog>

    <!-- 创建音色对话框 -->
    <el-dialog v-model="showVoiceDialog" title="新建音色" width="500px">
      <el-form label-position="top">
        <el-form-item label="音色名称">
          <el-input v-model="voiceForm.name" placeholder="请输入音色名称" />
        </el-form-item>
        <el-form-item label="音色描述">
          <el-input v-model="voiceForm.description" type="textarea" :rows="2" placeholder="请输入音色描述" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="voiceForm.gender" clearable placeholder="选择性别">
            <el-option label="男" value="male" />
            <el-option label="女" value="female" />
            <el-option label="中性" value="neutral" />
          </el-select>
        </el-form-item>
        <el-form-item label="语言">
          <el-select v-model="voiceForm.language" placeholder="选择语言">
            <el-option label="中文" value="zh" />
            <el-option label="英文" value="en" />
          </el-select>
        </el-form-item>
        <el-form-item label="归属文件夹">
          <el-select v-model="voiceForm.folderId" clearable placeholder="选择文件夹">
            <el-option
              v-for="folder in folders"
              :key="folder.folderId"
              :label="folder.name"
              :value="folder.folderId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showVoiceDialog = false">取消</el-button>
        <el-button type="primary" @click="saveVoice">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.folder-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.folder-item:hover {
  background-color: #f3f4f6;
}

.folder-item.active {
  background-color: #e0f2fe;
  color: #0284c7;
}

.asset-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.asset-card:hover {
  transform: translateY(-2px);
}
</style>