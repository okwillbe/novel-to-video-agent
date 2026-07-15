-- =====================================================
-- Novel to Video Agent - Preset Skills Data
-- Version: 1.0.0
-- =====================================================

USE `novel2video`;

-- =====================================================
-- Preset Skills
-- =====================================================

-- 1. Novel Analysis Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('novel-analysis_v1', '小说分析', '分析小说内容，提取角色、场景、剧情等核心元素', 'analysis', '---
name: novel-analysis
description: 分析小说内容，提取角色、场景、剧情等核心元素
version: 1.0.0
category: analysis
tags:
  - 小说
  - 分析
  - NLP
parameters:
  - name: content
    type: string
    required: true
    description: 小说文本内容
  - name: style
    type: string
    required: false
    default: \"武侠\"
    description: 小说风格类型
output:
  - characters: 角色列表及其特征
  - scenes: 场景列表及其描述
  - plot: 剧情主线及关键节点
  - style_elements: 风格元素分析
---

# 小说分析任务

你是一位专业的文学分析师，请分析以下小说内容，提取关键信息。

## 输入文本
{{content}}

## 分析要求
1. **角色提取**：识别所有主要和次要角色，提取其外貌、性格、关系等特征
2. **场景识别**：识别所有场景，包括环境描述、时间、氛围
3. **剧情梳理**：梳理主线剧情，识别关键情节点
4. **风格分析**：分析小说的写作风格、叙事特点

## 输出格式
请以 JSON 格式输出分析结果：
```json
{
  \"title\": \"小说标题\",
  \"genre\": \"类型\",
  \"characters\": [
    {
      \"name\": \"角色名\",
      \"role\": \"主角/配角\",
      \"appearance\": \"外貌描述\",
      \"personality\": \"性格特征\",
      \"relationships\": []
    }
  ],
  \"scenes\": [
    {
      \"name\": \"场景名\",
      \"description\": \"场景描述\",
      \"time\": \"时间\",
      \"atmosphere\": \"氛围\"
    }
  ],
  \"plot\": {
    \"main_arc\": \"主线\",
    \"key_events\": []
  },
  \"style_elements\": {
    \"tone\": \"基调\",
    \"pacing\": \"节奏\",
    \"visual_style\": \"视觉风格建议\"
  }
}
```
', '1.0.0', 'system', '["小说", "分析", "NLP"]', 1, 1);

-- 2. Character Extraction Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('character-extraction_v1', '角色提取', '从文本中提取角色信息及视觉特征', 'analysis', '---
name: character-extraction
description: 从文本中提取角色信息及视觉特征
version: 1.0.0
category: analysis
tags:
  - 角色
  - 提取
  - 视觉
parameters:
  - name: text
    type: string
    required: true
    description: 包含角色信息的文本
  - name: character_name
    type: string
    required: false
    description: 指定要提取的角色名
output:
  - character_profile: 角色档案
  - visual_description: 视觉描述（用于图片生成）
---

# 角色提取任务

请从以下文本中提取角色信息，特别关注可用于视觉创作的描述。

## 输入文本
{{text}}

{{#if character_name}}
## 目标角色
{{character_name}}
{{/if}}

## 提取要求
1. **基本信息**：姓名、年龄、性别、身份
2. **外貌特征**：
   - 面部特征（眼睛、鼻子、嘴巴、脸型）
   - 发型发色
   - 体型体态
   - 穿着打扮
3. **性格气质**：性格特点、习惯动作、神态表情
4. **标志性元素**：特殊标记、武器、饰品

## 输出格式
```json
{
  \"name\": \"角色名\",
  \"basic_info\": {
    \"age\": \"年龄\",
    \"gender\": \"性别\",
    \"identity\": \"身份\"
  },
  \"visual_description\": {
    \"face\": \"面部描述\",
    \"hair\": \"发型发色\",
    \"body\": \"体型描述\",
    \"clothing\": \"穿着打扮\",
    \"distinctive_features\": []
  },
  \"personality\": \"性格特点\",
  \"signature_elements\": [],
  \"image_prompt\": \"综合视觉描述，用于AI绘图\"
}
```
', '1.0.0', 'system', '["角色", "提取", "视觉"]', 1, 1);

-- 3. Scene Extraction Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('scene-extraction_v1', '场景提取', '识别和提取故事场景及环境描述', 'analysis', '---
name: scene-extraction
description: 识别和提取故事场景及环境描述
version: 1.0.0
category: analysis
tags:
  - 场景
  - 环境
  - 视觉
parameters:
  - name: text
    type: string
    required: true
    description: 包含场景信息的文本
output:
  - scenes: 场景列表
  - environment_descriptions: 环境描述
---

# 场景提取任务

请从以下文本中提取所有场景信息。

## 输入文本
{{text}}

## 提取要求
1. **场景识别**：识别所有不同的场景位置
2. **环境描述**：提取每个场景的环境细节
3. **时间信息**：识别场景的时间设定
4. **氛围感受**：分析场景的情感氛围
5. **视觉要素**：提取可用于视觉创作的关键元素

## 输出格式
```json
{
  \"scenes\": [
    {
      \"id\": \"scene_001\",
      \"name\": \"场景名称\",
      \"location_type\": \"室内/室外\",
      \"environment\": {
        \"setting\": \"具体位置\",
        \"time_of_day\": \"时间\",
        \"weather\": \"天气\",
        \"lighting\": \"光线\"
      },
      \"visual_elements\": {
        \"architecture\": \"建筑特征\",
        \"objects\": [\"物品列表\"],
        \"colors\": [\"主色调\"],
        \"atmosphere\": \"氛围描述\"
      },
      \"image_prompt\": \"场景视觉描述，用于AI绘图\"
    }
  ]
}
```
', '1.0.0', 'system', '["场景", "环境", "视觉"]', 1, 1);

-- 4. Script Generation Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('script-generation_v1', '剧本生成', '将小说/创意转化为剧本格式', 'generation', '---
name: script-generation
description: 将小说/创意转化为剧本格式，包含对话、动作、镜头指示
version: 1.0.0
category: generation
tags:
  - 剧本
  - 对话
  - 镜头
parameters:
  - name: content
    type: string
    required: true
    description: 小说内容或创意描述
  - name: duration_minutes
    type: number
    required: false
    default: 3
    description: 目标时长（分钟）
  - name: style
    type: string
    required: false
    default: \"武侠\"
    description: 剧本风格
output:
  - script: 剧本内容
  - scene_breakdown: 场景分解
---

# 剧本生成任务

请将以下内容转化为专业的影视剧本格式。

## 输入内容
{{content}}

## 创作要求
- 目标时长：{{duration_minutes}} 分钟
- 风格：{{style}}
- 时长参考：约每分钟150-200字台词

## 剧本格式要求
1. **场景标题**：使用标准格式（内景/外景 + 地点 + 时间）
2. **动作描述**：简洁描述人物动作和场景变化
3. **对话**：包含角色名和对话内容
4. **镜头指示**：关键镜头的拍摄建议
5. **转场**：场景间的转场方式

## 输出格式
```json
{
  \"title\": \"剧本标题\",
  \"duration_estimate\": \"预计时长\",
  \"total_scenes\": 场景数量,
  \"scenes\": [
    {
      \"scene_number\": 1,
      \"heading\": \"场景标题\",
      \"location\": \"地点\",
      \"time_of_day\": \"时间\",
      \"description\": \"场景描述\",
      \"beats\": [
        {
          \"type\": \"action/dialogue/camera\",
          \"character\": \"角色名\",
          \"content\": \"内容\",
          \"camera_direction\": \"镜头指示\"
        }
      ],
      \"duration_estimate_seconds\": 预计秒数
    }
  ],
  \"transitions\": [\"转场方式\"]
}
```
', '1.0.0', 'system', '["剧本", "对话", "镜头"]', 1, 1);

-- 5. Storyboard Design Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('storyboard-design_v1', '分镜设计', '根据剧本设计专业分镜方案', 'generation', '---
name: storyboard-design
description: 根据剧本设计专业分镜方案，包含镜头类型、运动、构图
version: 1.0.0
category: generation
tags:
  - 分镜
  - 镜头
  - 构图
parameters:
  - name: script
    type: object
    required: true
    description: 剧本对象（来自剧本生成）
  - name: style_preference
    type: string
    required: false
    default: \"电影感\"
    description: 分镜风格偏好
output:
  - storyboard: 分镜脚本
  - shot_list: 镜头列表
---

# 分镜设计任务

请根据以下剧本设计专业的分镜方案。

## 输入剧本
{{script}}

## 分镜要求
- 风格偏好：{{style_preference}}
- 每个场景提供多个镜头选择
- 考虑视觉连贯性和叙事节奏

## 镜头类型参考
- **远景(LS)**：建立场景，展示环境
- **全景(FS)**：展示人物全身和周围环境
- **中景(MS)**：腰部以上，常用对话
- **近景(CU)**：面部表情，情绪特写
- **特写(ECU)**：细节强调
- **运动镜头**：推、拉、摇、移、跟

## 输出格式
```json
{
  \"storyboard\": [
    {
      \"shot_number\": \"SC001_SH001\",
      \"scene_number\": 1,
      \"shot_type\": \"镜头类型\",
      \"shot_size\": \"景别\",
      \"camera_movement\": \"镜头运动\",
      \"angle\": \"拍摄角度\",
      \"composition\": \"构图说明\",
      \"duration_frames\": 帧数,
      \"description\": \"画面描述\",
      \"camera_direction\": \"摄像机指示\",
      \"notes\": \"备注\",
      \"image_prompt\": \"用于AI生成图片的提示词\"
    }
  ],
  \"shot_summary\": {
    \"total_shots\": 总镜头数,
    \"shot_types_distribution\": {},
    \"estimated_duration_seconds\": 预计总时长
  }
}
```
', '1.0.0', 'system', '["分镜", "镜头", "构图"]', 1, 1);

-- 6. Prompt Optimization Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('prompt-optimization_v1', '提示词优化', '优化图片/视频生成提示词', 'generation', '---
name: prompt-optimization
description: 优化图片/视频生成提示词，提升生成质量
version: 1.0.0
category: generation
tags:
  - 提示词
  - 优化
  - AI生成
parameters:
  - name: original_prompt
    type: string
    required: true
    description: 原始提示词
  - name: target_type
    type: string
    required: true
    description: 目标类型：image/video
  - name: style
    type: string
    required: false
    default: \"武侠\"
    description: 目标风格
  - name: reference_images
    type: array
    required: false
    description: 参考图片URL列表
output:
  - optimized_prompt: 优化后的提示词
  - negative_prompt: 负面提示词
  - style_modifiers: 风格修饰词
---

# 提示词优化任务

请优化以下AI生成提示词。

## 原始提示词
{{original_prompt}}

## 优化要求
- 目标类型：{{target_type}}
- 风格：{{style}}
- 增强细节描述
- 添加风格修饰词
- 确保生成一致性

## 优化原则
1. **具体化**：将模糊描述转化为具体视觉元素
2. **结构化**：使用逗号分隔的结构化格式
3. **风格化**：添加艺术风格、光影、质感等修饰词
4. **质量控制**：添加质量相关的关键词

## 输出格式
```json
{
  \"optimized_prompt\": \"优化后的主要提示词\",
  \"negative_prompt\": \"负面提示词，避免不想要的效果\",
  \"style_modifiers\": [\"风格修饰词列表\"],
  \"quality_modifiers\": [\"质量修饰词\"],
  \"composition_hints\": [\"构图提示\"],
  \"suggested_parameters\": {
    \"steps\": 推荐步数,
    \"cfg_scale\": 推荐CFG值,
    \"sampler\": \"推荐采样器\"
  }
}
```
', '1.0.0', 'system', '["提示词", "优化", "AI生成"]', 1, 1);

-- 7. Image Generation Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('image-generation_v1', '图片生成', '生成角色/场景图片', 'synthesis', '---
name: image-generation
description: 根据描述生成角色或场景图片
version: 1.0.0
category: synthesis
tags:
  - 图片
  - AI生成
  - 角色
  - 场景
parameters:
  - name: prompt
    type: string
    required: true
    description: 图片描述
  - name: image_type
    type: string
    required: true
    description: 类型：character/scene
  - name: style
    type: string
    required: false
    default: \"武侠写实\"
    description: 图片风格
  - name: aspect_ratio
    type: string
    required: false
    default: \"16:9\"
    description: 宽高比
  - name: reference_image
    type: string
    required: false
    description: 参考图片URL（用于一致性）
output:
  - image_urls: 生成的图片URL列表
  - metadata: 图片元数据
---

# 图片生成任务

请生成以下类型的图片。

## 生成要求
- 描述：{{prompt}}
- 类型：{{image_type}}
- 风格：{{style}}
- 宽高比：{{aspect_ratio}}

{{#if reference_image}}
## 参考图片
{{reference_image}}
{{/if}}

## 执行流程
1. 解析描述，提取关键视觉元素
2. 根据类型和风格优化提示词
3. 调用图片生成API
4. 验证生成结果
5. 如有参考图，确保风格一致性

## 输出格式
```json
{
  \"images\": [
    {
      \"url\": \"图片URL\",
      \"width\": 宽度,
      \"height\": 高度,
      \"seed\": 种子值,
      \"model_used\": \"使用的模型\"
    }
  ],
  \"prompt_used\": \"实际使用的提示词\",
  \"generation_params\": {}
}
```
', '1.0.0', 'system', '["图片", "AI生成", "角色", "场景"]', 1, 1);

-- 8. Video Synthesis Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('video-synthesis_v1', '视频合成', '将分镜合成为视频', 'synthesis', '---
name: video-synthesis
description: 将分镜图片和参数合成为视频
version: 1.0.0
category: synthesis
tags:
  - 视频
  - 合成
  - AI生成
parameters:
  - name: shots
    type: array
    required: true
    description: 镜头图片列表及参数
  - name: audio_track
    type: string
    required: false
    description: 音轨URL
  - name: transition_type
    type: string
    required: false
    default: \"fade\"
    description: 转场类型
  - name: output_format
    type: string
    required: false
    default: \"mp4\"
    description: 输出格式
output:
  - video_url: 生成的视频URL
  - duration: 视频时长
---

# 视频合成任务

请将以下分镜素材合成为完整视频。

## 输入素材
{{shots}}

{{#if audio_track}}
## 音轨
{{audio_track}}
{{/if}}

## 合成要求
- 转场类型：{{transition_type}}
- 输出格式：{{output_format}}

## 执行流程
1. 验证所有素材可用性
2. 按顺序排列镜头
3. 添加转场效果
4. 同步音轨（如有）
5. 渲染输出视频

## 输出格式
```json
{
  \"video_url\": \"视频URL\",
  \"duration_seconds\": 时长秒数,
  \"resolution\": \"分辨率\",
  \"file_size_bytes\": 文件大小,
  \"shots_used\": 使用镜头数
}
```
', '1.0.0', 'system', '["视频", "合成", "AI生成"]', 1, 1);

-- 9. Voice Generation Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('voice-generation_v1', '配音生成', '为角色生成配音', 'synthesis', '---
name: voice-generation
description: 根据文本和角色特征生成语音配音
version: 1.0.0
category: synthesis
tags:
  - 配音
  - 语音
  - TTS
parameters:
  - name: text
    type: string
    required: true
    description: 要转换为语音的文本
  - name: character
    type: object
    required: false
    description: 角色信息（用于选择合适的声音）
  - name: voice_style
    type: string
    required: false
    default: \"自然\"
    description: 声音风格
  - name: language
    type: string
    required: false
    default: \"zh-CN\"
    description: 语言
output:
  - audio_url: 生成的音频URL
  - duration: 音频时长
---

# 配音生成任务

请生成以下文本的语音配音。

## 文本内容
{{text}}

{{#if character}}
## 角色信息
{{character}}
{{/if}}

## 配音要求
- 声音风格：{{voice_style}}
- 语言：{{language}}

## 声音选择原则
1. 根据角色性别、年龄选择合适的声音
2. 根据角色性格调整语调和节奏
3. 根据场景情绪调整语气

## 输出格式
```json
{
  \"audio_url\": \"音频URL\",
  \"duration_seconds\": 时长秒数,
  \"voice_used\": \"使用的声音\",
  \"sample_rate\": 采样率
}
```
', '1.0.0', 'system', '["配音", "语音", "TTS"]', 1, 1);

-- 10. Consistency Check Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('consistency-check_v1', '一致性校验', '校验角色/场景视觉一致性', 'postprocess', '---
name: consistency-check
description: 使用MLLM校验生成图片的视觉一致性
version: 1.0.0
category: postprocess
tags:
  - 一致性
  - 校验
  - MLLM
parameters:
  - name: reference_image
    type: string
    required: true
    description: 参考图片URL
  - name: candidate_images
    type: array
    required: true
    description: 待校验图片URL列表
  - name: consistency_type
    type: string
    required: false
    default: \"character\"
    description: 一致性类型：character/scene
output:
  - scores: 一致性评分列表
  - best_match: 最佳匹配
---

# 一致性校验任务

请校验以下图片的视觉一致性。

## 参考图片
{{reference_image}}

## 待校验图片
{{candidate_images}}

## 校验要求
- 一致性类型：{{consistency_type}}

## 校验维度
### 角色一致性
1. 面部特征相似度
2. 发型发色一致性
3. 体型特征一致性
4. 标志性元素一致性

### 场景一致性
1. 环境结构相似度
2. 光影氛围一致性
3. 色调风格一致性

## 输出格式
```json
{
  \"scores\": [
    {
      \"image_url\": \"图片URL\",
      \"overall_score\": 总体评分0-1,
      \"detail_scores\": {
        \"face_similarity\": 面部相似度,
        \"body_consistency\": 体型一致性,
        \"style_match\": 风格匹配度
      },
      \"pass\": 是否通过阈值
    }
  ],
  \"best_match\": {
    \"image_url\": \"最佳匹配URL\",
    \"score\": 评分
  },
  \"recommendation\": \"建议说明\"
}
```
', '1.0.0', 'system', '["一致性", "校验", "MLLM"]', 1, 1);

-- 11. Style Transfer Skill
INSERT INTO `skills` (`skill_id`, `name`, `description`, `category`, `skill_content`, `version`, `author`, `tags`, `status`, `is_public`) VALUES
('style-transfer_v1', '风格迁移', '统一视觉风格', 'postprocess', '---
name: style-transfer
description: 将生成内容统一到指定视觉风格
version: 1.0.0
category: postprocess
tags:
  - 风格
  - 迁移
  - 视觉统一
parameters:
  - name: images
    type: array
    required: true
    description: 待处理的图片URL列表
  - name: target_style
    type: string
    required: true
    description: 目标风格描述
  - name: reference_style_image
    type: string
    required: false
    description: 风格参考图URL
output:
  - styled_images: 处理后的图片URL列表
---

# 风格迁移任务

请将以下图片统一到指定风格。

## 待处理图片
{{images}}

## 目标风格
{{target_style}}

{{#if reference_style_image}}
## 风格参考图
{{reference_style_image}}
{{/if}}

## 处理要求
1. 保持原图主体内容
2. 统一色调和光影
3. 统一画风风格
4. 确保系列图片风格一致

## 输出格式
```json
{
  \"styled_images\": [
    {
      \"original_url\": \"原图URL\",
      \"styled_url\": \"处理后URL\",
      \"style_strength\": 风格强度
    }
  ],
  \"style_coherence_score\": 系列风格一致性评分
}
```
', '1.0.0', 'system', '["风格", "迁移", "视觉统一"]', 1, 1);

-- =====================================================
-- Initialize Skill Versions History
-- =====================================================

INSERT INTO `skill_versions` (`skill_id`, `version`, `change_type`, `change_summary`, `skill_content`)
SELECT `skill_id`, `version`, 'create', '初始创建', `skill_content`
FROM `skills`;
