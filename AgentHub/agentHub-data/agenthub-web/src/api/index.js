import api from '@/utils/api'

/**
 * Card 相关 API
 */
export const cardApi = {
  getList(params) {
    return api.get('/cards', { params })
  },

  search(keyword, pageNum = 1, pageSize = 10) {
    return api.get('/cards/search', { params: { keyword, pageNum, pageSize } })
  },

  getDetail(id) {
    return api.get(`/cards/${id}`)
  },

  getContent(id) {
    return api.get(`/cards/${id}/content`)
  },

  create(data) {
    return api.post('/cards', data)
  },

  update(id, data) {
    return api.put(`/cards/${id}`, data)
  },

  delete(id) {
    return api.delete(`/cards/${id}`)
  },

  publish(id) {
    return api.post(`/cards/${id}/publish`)
  },

  archive(id) {
    return api.post(`/cards/${id}/archive`)
  },

  getByAgent(agentId, pageNum = 1, pageSize = 10) {
    return api.get(`/cards/agent/${agentId}`, { params: { pageNum, pageSize } })
  },

  unarchive(id) {
    return api.post(`/cards/${id}/unarchive`)
  },

  like(id) {
    return api.post(`/cards/${id}/like`)
  }
}

/**
 * Skill 相关 API
 */
export const skillApi = {
  getByCardId(cardId) {
    return api.get(`/cards/${cardId}/skills`)
  },

  create(cardId, data) {
    return api.post(`/cards/${cardId}/skills`, data)
  },

  update(cardId, id, data) {
    return api.put(`/cards/${cardId}/skills/${id}`, data)
  },

  delete(cardId, id) {
    return api.delete(`/cards/${cardId}/skills/${id}`)
  }
}

/**
 * 评论相关 API
 */
export const commentApi = {
  getCardComments(cardId, pageNum = 1, pageSize = 10) {
    return api.get(`/comments/card/${cardId}`, { params: { pageNum, pageSize } })
  },

  getReplies(rootId, pageNum = 1, pageSize = 10) {
    return api.get(`/comments/${rootId}/replies`, { params: { pageNum, pageSize } })
  },

  create(data) {
    return api.post('/comments', data)
  },

  delete(id) {
    return api.delete(`/comments/${id}`)
  },

  like(id) {
    return api.post(`/comments/${id}/like`)
  },

  getCount(cardId) {
    return api.get(`/comments/card/${cardId}/count`)
  },

  getById(id) {
    return api.get(`/comments/${id}`)
  },

  approve(id) {
    return api.post(`/comments/${id}/approve`)
  },

  reject(id, reason) {
    return api.post(`/comments/${id}/reject?reason=${encodeURIComponent(reason)}`)
  },

  getPending(pageNum = 1, pageSize = 10) {
    return api.get('/comments/pending', { params: { pageNum, pageSize } })
  }
}

/**
 * 点赞相关 API
 */
export const likeApi = {
  like(data) {
    return api.post('/likes', data)
  },

  unlike(userId, userType, targetId, targetType) {
    return api.delete('/likes', { params: { userId, userType, targetId, targetType } })
  },

  getCount(targetId, targetType) {
    return api.get('/likes/count', { params: { targetId, targetType } })
  },

  check(userId, userType, targetId, targetType) {
    return api.get('/likes/check', { params: { userId, userType, targetId, targetType } })
  },

  getStatus(userId, userType, targetId, targetType) {
    return api.get('/likes/status', { params: { userId, userType, targetId, targetType } })
  },

  getTargetLikes(targetId, targetType, pageNum = 1, pageSize = 10) {
    return api.get(`/likes/target/${targetId}`, { params: { targetType, pageNum, pageSize } })
  },

  batchCheck(userId, userType, targetIds, targetType) {
    return api.post('/likes/batch-check', targetIds, { params: { userId, userType, targetType } })
  }
}

/**
 * Agent 相关 API（只读，Agent 注册通过 CLI 完成）
 */
export const agentApi = {
  getAll() {
    return api.get('/agents')
  },

  getById(id) {
    return api.get(`/agents/${id}`)
  },

  getByName(name) {
    return api.get(`/agents/name/${encodeURIComponent(name)}`)
  },

  getByStatus(status) {
    return api.get(`/agents/status/${status}`)
  },

  query(params) {
    return api.get('/agents/query', { params })
  },

  count() {
    return api.get('/agents/count')
  },

  countByStatus(status) {
    return api.get(`/agents/count/${status}`)
  }
}

/**
 * 评分相关 API
 */
export const scoreApi = {
  score(data) {
    return api.post('/scores', data)
  },

  update(data) {
    return api.put('/scores', data)
  },

  getStatus(params) {
    return api.get('/scores/status', { params })
  },

  delete(params) {
    return api.delete('/scores', { params })
  }
}

/**
 * 收藏相关 API
 */
export const favoriteApi = {
  // 收藏夹
  createFolder(data) {
    return api.post('/favorites/folders', data)
  },

  updateFolder(id, data) {
    return api.put(`/favorites/folders/${id}`, data)
  },

  deleteFolder(id, userId, userType) {
    return api.delete(`/favorites/folders/${id}`, { params: { userId, userType } })
  },

  getFolders(params) {
    return api.get('/favorites/folders', { params })
  },

  // 收藏项
  add(data) {
    return api.post('/favorites', data)
  },

  remove(params) {
    return api.delete('/favorites', { params })
  },

  getStatus(params) {
    return api.get('/favorites/status', { params })
  },

  move(userId, userType, targetId, targetType, newFolderId) {
    return api.put('/favorites/move', null, { params: { userId, userType, targetId, targetType, newFolderId } })
  },

  getFolderById(id) {
    return api.get(`/favorites/folders/${id}`)
  }
}

/**
 * 请求日志 API
 */
export const logApi = {
  getList(params) {
    return api.get('/logs', { params })
  },

  getById(id) {
    return api.get(`/logs/${id}`)
  },

  getByTraceId(traceId) {
    return api.get(`/logs/trace/${traceId}`)
  },

  clean() {
    return api.delete('/logs/clean')
  }
}
