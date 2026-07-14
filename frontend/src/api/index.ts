import { axios } from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Response interceptor
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(message))
  }
)

// Types
export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export interface UserInfo {
  userId: string
  email: string
  nickname: string
  role: string
  quotaVideoSeconds: number
  quotaImageCount: number
  quotaVoiceSeconds: number
  quotaTextTokens: number
}

export interface TaskCreatedResponse {
  taskId: string
  status: string
  estimatedTime: number
}

export interface TaskStatusResponse {
  taskId: string
  status: string
  progress: number
  currentStep: string
  steps: StepInfo[]
}

export interface StepInfo {
  name: string
  status: string
  progress: number
}

export interface TaskSummary {
  taskId: string
  taskType: string
  status: string
  progress: number
  createdAt: string
}

export interface SkillResponse {
  skillId: string
  name: string
  description: string
  category: string
  version: string
  tags: string[]
  useCount: number
  avgRating: number | null
}

// ===== User API (单用户模式) =====

export const getCurrentUser = () =>
  api.get<ApiResponse<UserInfo>>('/user')

export const login = (email: string, password: string) =>
  api.post<ApiResponse<{ userId: string; nickname: string; role: string; token: string }>>('/login', { email, password })

// ===== Task API =====

export const generateVideo = (data: {
  content: string
  style?: string
  duration?: number
  options?: Record<string, any>
}) => api.post<ApiResponse<TaskCreatedResponse>>('/generate', data)

export const getTaskStatus = (taskId: string) =>
  api.get<ApiResponse<TaskStatusResponse>>(`/tasks/${taskId}`)

export const getTasks = (limit = 20) =>
  api.get<ApiResponse<TaskSummary[]>>(`/tasks?limit=${limit}`)

export const cancelTask = (taskId: string) =>
  api.post<ApiResponse<void>>(`/tasks/${taskId}/cancel`)

// ===== Skills API =====

export const getSkills = (category?: string, keyword?: string) => {
  const params = new URLSearchParams()
  if (category) params.append('category', category)
  if (keyword) params.append('keyword', keyword)
  return api.get<ApiResponse<SkillResponse[]>>(`/skills?${params.toString()}`)
}

export const getPopularSkills = (limit = 10) =>
  api.get<ApiResponse<SkillResponse[]>>(`/skills/popular?limit=${limit}`)

export const getSkill = (skillId: string, version = '1.0.0') =>
  api.get<ApiResponse<SkillResponse>>(`/skills/${skillId}?version=${version}`)

export default api
