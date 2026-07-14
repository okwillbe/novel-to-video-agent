import api, { type ApiResponse } from './index'

// ==================== 类型定义 ====================

export interface Folder {
  folderId: string
  name: string
  sortOrder: number
  createdAt: string
}

export interface Character {
  characterId: string
  folderId: string | null
  name: string
  aliases: string[] | null
  profileData: object | null
  profileConfirmed: boolean
  voiceId: string | null
  voiceType: string | null
  globalVoiceId: string | null
  createdAt: string
  appearances: Appearance[] | null
}

export interface Appearance {
  appearanceId: string
  appearanceIndex: number
  changeReason: string | null
  artStyle: string | null
  description: string | null
  imageUrl: string | null
  imageUrls: string[] | null
  selectedIndex: number
  createdAt: string
}

export interface Location {
  locationId: string
  folderId: string | null
  name: string
  summary: string | null
  artStyle: string | null
  createdAt: string
  images: LocationImage[] | null
}

export interface LocationImage {
  imageId: string
  imageIndex: number
  description: string | null
  imageUrl: string | null
  isSelected: boolean
  createdAt: string
}

export interface Voice {
  voiceId: string
  folderId: string | null
  name: string
  description: string | null
  qwenVoiceId: string | null
  voiceType: string
  customVoiceUrl: string | null
  voicePrompt: string | null
  gender: string | null
  language: string
  createdAt: string
}

// ==================== 文件夹 API ====================

export const getFolders = () =>
  api.get<ApiResponse<Folder[]>>('/assets/folders')

export const createFolder = (data: { name: string; sortOrder?: number }) =>
  api.post<ApiResponse<Folder>>('/assets/folders', data)

export const deleteFolder = (folderId: string) =>
  api.delete<ApiResponse<void>>(`/assets/folders/${folderId}`)

// ==================== 角色 API ====================

export const getCharacters = (folderId?: string) => {
  const params = folderId ? `?folderId=${folderId}` : ''
  return api.get<ApiResponse<Character[]>>(`/assets/characters${params}`)
}

export const getCharacter = (characterId: string) =>
  api.get<ApiResponse<Character>>(`/assets/characters/${characterId}`)

export const createCharacter = (data: {
  folderId?: string
  name: string
  aliases?: string[]
  profileData?: object
  profileConfirmed?: boolean
  voiceId?: string
  voiceType?: string
  customVoiceUrl?: string
  globalVoiceId?: string
}) => api.post<ApiResponse<Character>>('/assets/characters', data)

export const updateCharacter = (characterId: string, data: Partial<{
  folderId: string | null
  name: string
  aliases: string[]
  profileData: object
  profileConfirmed: boolean
  voiceId: string
  voiceType: string
  customVoiceUrl: string
  globalVoiceId: string
}>) => api.put<ApiResponse<Character>>(`/assets/characters/${characterId}`, data)

export const deleteCharacter = (characterId: string) =>
  api.delete<ApiResponse<void>>(`/assets/characters/${characterId}`)

// ==================== 角色形象 API ====================

export const getAppearances = (characterId: string) =>
  api.get<ApiResponse<Appearance[]>>(`/assets/characters/${characterId}/appearances`)

export const createAppearance = (characterId: string, data: {
  appearanceIndex?: number
  changeReason?: string
  artStyle?: string
  description?: string
  imageUrl?: string
  imageUrls?: string[]
  selectedIndex?: number
}) => api.post<ApiResponse<Appearance>>(`/assets/characters/${characterId}/appearances`, data)

export const deleteAppearance = (appearanceId: string) =>
  api.delete<ApiResponse<void>>(`/assets/appearances/${appearanceId}`)

// ==================== 场景 API ====================

export const getLocations = (folderId?: string) => {
  const params = folderId ? `?folderId=${folderId}` : ''
  return api.get<ApiResponse<Location[]>>(`/assets/locations${params}`)
}

export const getLocation = (locationId: string) =>
  api.get<ApiResponse<Location>>(`/assets/locations/${locationId}`)

export const createLocation = (data: {
  folderId?: string
  name: string
  summary?: string
  artStyle?: string
}) => api.post<ApiResponse<Location>>('/assets/locations', data)

export const updateLocation = (locationId: string, data: Partial<{
  folderId: string | null
  name: string
  summary: string
  artStyle: string
}>) => api.put<ApiResponse<Location>>(`/assets/locations/${locationId}`, data)

export const deleteLocation = (locationId: string) =>
  api.delete<ApiResponse<void>>(`/assets/locations/${locationId}`)

// ==================== 场景图片 API ====================

export const getLocationImages = (locationId: string) =>
  api.get<ApiResponse<LocationImage[]>>(`/assets/locations/${locationId}/images`)

export const createLocationImage = (locationId: string, data: {
  imageIndex?: number
  description?: string
  imageUrl?: string
  isSelected?: boolean
}) => api.post<ApiResponse<LocationImage>>(`/assets/locations/${locationId}/images`, data)

export const deleteLocationImage = (imageId: string) =>
  api.delete<ApiResponse<void>>(`/assets/location-images/${imageId}`)

// ==================== 音色 API ====================

export const getVoices = (folderId?: string) => {
  const params = folderId ? `?folderId=${folderId}` : ''
  return api.get<ApiResponse<Voice[]>>(`/assets/voices${params}`)
}

export const getVoice = (voiceId: string) =>
  api.get<ApiResponse<Voice>>(`/assets/voices/${voiceId}`)

export const createVoice = (data: {
  folderId?: string
  name: string
  description?: string
  qwenVoiceId?: string
  voiceType?: string
  customVoiceUrl?: string
  voicePrompt?: string
  gender?: string
  language?: string
}) => api.post<ApiResponse<Voice>>('/assets/voices', data)

export const updateVoice = (voiceId: string, data: Partial<{
  folderId: string | null
  name: string
  description: string
  qwenVoiceId: string
  voiceType: string
  customVoiceUrl: string
  voicePrompt: string
  gender: string
  language: string
}>) => api.put<ApiResponse<Voice>>(`/assets/voices/${voiceId}`, data)

export const deleteVoice = (voiceId: string) =>
  api.delete<ApiResponse<void>>(`/assets/voices/${voiceId}`)