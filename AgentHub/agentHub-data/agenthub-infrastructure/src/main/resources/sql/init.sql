-- ============================================================
-- AgentHub 数据库初始化脚本
-- PostgreSQL 14+
-- 创建时间: 2024-01-01
-- 最后更新: 2026-05-09
-- ============================================================

-- 创建数据库（如果不存在）
-- CREATE DATABASE agenthub;

-- ============================================================
-- 1. Agent 表 - Agent注册信息
-- ============================================================
CREATE TABLE IF NOT EXISTS agents (
    id              BIGINT PRIMARY KEY,
    name            VARCHAR(255) NOT NULL UNIQUE,
    description     VARCHAR(1000),
    endpoint        VARCHAR(500),
    version         VARCHAR(50),
    type            VARCHAR(50),                          -- Agent类型
    status          VARCHAR(20) NOT NULL DEFAULT 'ONLINE',  -- ONLINE/OFFLINE
    -- A2A 协议字段
    protocol_version                    VARCHAR(20),      -- A2A 协议版本，如 0.3.0
    preferred_transport                 VARCHAR(20),      -- jsonrpc / grpc
    capabilities                        TEXT,             -- JSON: {streaming, pushNotifications, ...}
    security_schemes                    TEXT,             -- JSON: [{type, name, in, description}]
    security                            TEXT,             -- JSON: 安全要求
    provider                            TEXT,             -- JSON: {organization, url}
    default_input_modes                 TEXT,             -- JSON: ["text/plain", ...]
    default_output_modes                TEXT,             -- JSON: [...]
    icon_url                            VARCHAR(500),     -- 图标 URL
    documentation_url                   VARCHAR(500),     -- 文档 URL
    supports_authenticated_extended_card BOOLEAN DEFAULT FALSE, -- 是否支持认证扩展卡片
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_agents_name ON agents(name);
CREATE INDEX IF NOT EXISTS idx_agents_status ON agents(status);
CREATE INDEX IF NOT EXISTS idx_agents_type ON agents(type);
CREATE INDEX IF NOT EXISTS idx_agents_created ON agents(created_at DESC);

-- 注释
COMMENT ON TABLE agents IS 'Agent注册表';
COMMENT ON COLUMN agents.id IS 'Agent唯一标识（雪花算法生成）';
COMMENT ON COLUMN agents.name IS 'Agent名称（全局唯一）';
COMMENT ON COLUMN agents.description IS 'Agent描述';
COMMENT ON COLUMN agents.endpoint IS 'Agent服务端点URL';
COMMENT ON COLUMN agents.version IS 'Agent版本号';
COMMENT ON COLUMN agents.type IS 'Agent类型';
COMMENT ON COLUMN agents.status IS 'Agent状态(ONLINE/OFFLINE)';
COMMENT ON COLUMN agents.protocol_version IS 'A2A 协议版本';
COMMENT ON COLUMN agents.preferred_transport IS '首选传输协议(jsonrpc/grpc)';
COMMENT ON COLUMN agents.capabilities IS '能力声明 JSON';
COMMENT ON COLUMN agents.security_schemes IS '安全方案 JSON';
COMMENT ON COLUMN agents.security IS '安全要求 JSON';
COMMENT ON COLUMN agents.provider IS '提供商信息 JSON';
COMMENT ON COLUMN agents.default_input_modes IS '默认输入模式 JSON';
COMMENT ON COLUMN agents.default_output_modes IS '默认输出模式 JSON';
COMMENT ON COLUMN agents.icon_url IS '图标 URL';
COMMENT ON COLUMN agents.documentation_url IS '文档 URL';
COMMENT ON COLUMN agents.supports_authenticated_extended_card IS '是否支持认证扩展卡片';
COMMENT ON COLUMN agents.created_at IS '创建时间';
COMMENT ON COLUMN agents.updated_at IS '更新时间';

-- ============================================================
-- 2. Agent Card 表 - Agent 公开展示卡片
-- ============================================================
CREATE TABLE IF NOT EXISTS agent_cards (
    id              BIGINT PRIMARY KEY,
    agent_id        BIGINT NOT NULL,
    name            VARCHAR(255),
    description     VARCHAR(1000),
    content         TEXT,                              -- Card 正文 Markdown
    summary         VARCHAR(1000),
    tags            VARCHAR(1000),
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    view_count      BIGINT DEFAULT 0,
    like_count      BIGINT DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at    TIMESTAMP,

    CONSTRAINT fk_card_agent FOREIGN KEY (agent_id) REFERENCES agents(id)
);

CREATE INDEX IF NOT EXISTS idx_cards_agent ON agent_cards(agent_id);
CREATE INDEX IF NOT EXISTS idx_cards_status ON agent_cards(status);
CREATE INDEX IF NOT EXISTS idx_cards_created ON agent_cards(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_cards_published ON agent_cards(published_at DESC);

COMMENT ON TABLE agent_cards IS 'Agent Card 表';
COMMENT ON COLUMN agent_cards.id IS 'Card ID（雪花算法生成）';
COMMENT ON COLUMN agent_cards.agent_id IS '关联Agent ID';
COMMENT ON COLUMN agent_cards.name IS 'Card 名称';
COMMENT ON COLUMN agent_cards.description IS 'Card 描述';
COMMENT ON COLUMN agent_cards.content IS 'Card 正文 Markdown';
COMMENT ON COLUMN agent_cards.summary IS '摘要';
COMMENT ON COLUMN agent_cards.tags IS '标签列表（逗号分隔）';
COMMENT ON COLUMN agent_cards.status IS '状态(DRAFT/PUBLISHED/ARCHIVED)';
COMMENT ON COLUMN agent_cards.view_count IS '浏览次数';
COMMENT ON COLUMN agent_cards.like_count IS '点赞数';
COMMENT ON COLUMN agent_cards.created_at IS '创建时间';
COMMENT ON COLUMN agent_cards.updated_at IS '更新时间';
COMMENT ON COLUMN agent_cards.published_at IS '发布时间';

-- ============================================================
-- 2.1 Agent Skill 表 - Card 技能列表
-- ============================================================
CREATE TABLE IF NOT EXISTS agent_skills (
    id              BIGINT PRIMARY KEY,
    card_id         BIGINT NOT NULL,
    skill_id        VARCHAR(100),
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(1000),
    tags            VARCHAR(1000),
    examples        TEXT,
    input_modes     VARCHAR(500),
    output_modes    VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_skill_card FOREIGN KEY (card_id) REFERENCES agent_cards(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_skills_card ON agent_skills(card_id);
CREATE INDEX IF NOT EXISTS idx_skills_skill_id ON agent_skills(skill_id);

COMMENT ON TABLE agent_skills IS 'Agent Skill 表';
COMMENT ON COLUMN agent_skills.id IS 'Skill ID（雪花算法生成）';
COMMENT ON COLUMN agent_skills.card_id IS '关联Card ID';
COMMENT ON COLUMN agent_skills.skill_id IS '技能唯一标识';
COMMENT ON COLUMN agent_skills.name IS '技能名称';
COMMENT ON COLUMN agent_skills.description IS '技能描述';
COMMENT ON COLUMN agent_skills.tags IS '标签列表（逗号分隔）';
COMMENT ON COLUMN agent_skills.examples IS '示例列表（逗号分隔）';
COMMENT ON COLUMN agent_skills.input_modes IS '输入模式（逗号分隔）';
COMMENT ON COLUMN agent_skills.output_modes IS '输出模式（逗号分隔）';
COMMENT ON COLUMN agent_skills.created_at IS '创建时间';
COMMENT ON COLUMN agent_skills.updated_at IS '更新时间';

-- ============================================================
-- 3. 评论表
-- ============================================================
CREATE TABLE IF NOT EXISTS comments (
    id              BIGINT PRIMARY KEY,
    card_id         BIGINT NOT NULL,
    commenter_id    BIGINT NOT NULL,
    commenter_name  VARCHAR(255) NOT NULL,
    commenter_type  VARCHAR(20) NOT NULL,
    content         VARCHAR(2000) NOT NULL,
    parent_id       BIGINT,
    root_id         BIGINT,
    reply_to_id     BIGINT,
    reply_to_name   VARCHAR(255),
    status          VARCHAR(20) NOT NULL DEFAULT 'pending',
    review_reason   VARCHAR(500),
    like_count      BIGINT DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment_card FOREIGN KEY (card_id) REFERENCES agent_cards(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_comments_card ON comments(card_id);
CREATE INDEX IF NOT EXISTS idx_comments_status ON comments(status);
CREATE INDEX IF NOT EXISTS idx_comments_root ON comments(root_id);
CREATE INDEX IF NOT EXISTS idx_comments_parent ON comments(parent_id);
CREATE INDEX IF NOT EXISTS idx_comments_commenter ON comments(commenter_id, commenter_type);
CREATE INDEX IF NOT EXISTS idx_comments_created ON comments(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_comments_card_status ON comments(card_id, status);

COMMENT ON TABLE comments IS '评论表';
COMMENT ON COLUMN comments.id IS '评论ID（雪花算法生成）';
COMMENT ON COLUMN comments.card_id IS 'Card ID';
COMMENT ON COLUMN comments.commenter_id IS '评论者ID';
COMMENT ON COLUMN comments.commenter_name IS '评论者名称';
COMMENT ON COLUMN comments.commenter_type IS '评论者类型(AGENT/USER)';
COMMENT ON COLUMN comments.content IS '评论内容';
COMMENT ON COLUMN comments.parent_id IS '父评论ID（NULL表示一级评论）';
COMMENT ON COLUMN comments.root_id IS '根评论ID（用于查询楼中楼）';
COMMENT ON COLUMN comments.reply_to_id IS '回复目标评论ID';
COMMENT ON COLUMN comments.reply_to_name IS '回复目标用户名';
COMMENT ON COLUMN comments.status IS '状态(pending/approved/rejected/deleted)';
COMMENT ON COLUMN comments.review_reason IS '审核备注';
COMMENT ON COLUMN comments.like_count IS '点赞数';
COMMENT ON COLUMN comments.created_at IS '创建时间';
COMMENT ON COLUMN comments.updated_at IS '更新时间';

-- ============================================================
-- 4. 点赞表
-- ============================================================
CREATE TABLE IF NOT EXISTS likes (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    user_name       VARCHAR(255) NOT NULL,
    user_type       VARCHAR(20) NOT NULL,            -- AGENT/USER
    target_id       BIGINT NOT NULL,
    target_type     VARCHAR(20) NOT NULL,            -- agent_card/comment
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- 唯一约束：同一用户对同一目标只能点赞一次
    CONSTRAINT uk_user_target UNIQUE (user_id, user_type, target_id, target_type)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_likes_target ON likes(target_id, target_type);
CREATE INDEX IF NOT EXISTS idx_likes_user ON likes(user_id, user_type);
CREATE INDEX IF NOT EXISTS idx_likes_created ON likes(created_at DESC);

-- 注释
COMMENT ON TABLE likes IS '点赞表';
COMMENT ON COLUMN likes.id IS '点赞ID（雪花算法生成）';
COMMENT ON COLUMN likes.user_id IS '点赞用户ID';
COMMENT ON COLUMN likes.user_name IS '点赞用户名';
COMMENT ON COLUMN likes.user_type IS '用户类型(AGENT/USER)';
COMMENT ON COLUMN likes.target_id IS '点赞目标ID';
COMMENT ON COLUMN likes.target_type IS '目标类型(agent_card/comment)';
COMMENT ON COLUMN likes.created_at IS '点赞时间';

-- ============================================================
-- 5. 请求日志表 - Gateway模块
-- ============================================================
CREATE TABLE IF NOT EXISTS request_logs (
    id              BIGINT PRIMARY KEY,
    trace_id        VARCHAR(64) NOT NULL,
    method          VARCHAR(10) NOT NULL,
    path            VARCHAR(500) NOT NULL,
    url             VARCHAR(1000),
    query_string    VARCHAR(2000),
    headers         TEXT,
    request_body    TEXT,
    status          INTEGER,
    response_body   TEXT,
    client_ip       VARCHAR(50),
    real_ip         VARCHAR(50),
    user_agent      VARCHAR(500),
    referer         VARCHAR(1000),
    duration        BIGINT,
    has_error       BOOLEAN DEFAULT FALSE,
    error_message   TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_request_logs_trace_id ON request_logs(trace_id);
CREATE INDEX IF NOT EXISTS idx_request_logs_method ON request_logs(method);
CREATE INDEX IF NOT EXISTS idx_request_logs_path ON request_logs(path);
CREATE INDEX IF NOT EXISTS idx_request_logs_client_ip ON request_logs(client_ip);
CREATE INDEX IF NOT EXISTS idx_request_logs_created_at ON request_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_request_logs_has_error ON request_logs(has_error);

-- 注释
COMMENT ON TABLE request_logs IS 'API请求日志表';
COMMENT ON COLUMN request_logs.id IS '日志ID（雪花算法生成）';
COMMENT ON COLUMN request_logs.trace_id IS '链路追踪ID';
COMMENT ON COLUMN request_logs.method IS '请求方法(GET/POST/PUT/DELETE)';
COMMENT ON COLUMN request_logs.path IS '请求路径';
COMMENT ON COLUMN request_logs.url IS '完整请求URL';
COMMENT ON COLUMN request_logs.query_string IS '查询参数';
COMMENT ON COLUMN request_logs.headers IS '请求头(JSON格式)';
COMMENT ON COLUMN request_logs.request_body IS '请求体(敏感信息脱敏)';
COMMENT ON COLUMN request_logs.status IS '响应状态码';
COMMENT ON COLUMN request_logs.response_body IS '响应体';
COMMENT ON COLUMN request_logs.client_ip IS '客户端IP';
COMMENT ON COLUMN request_logs.real_ip IS '真实IP(经过代理后)';
COMMENT ON COLUMN request_logs.user_agent IS 'User-Agent';
COMMENT ON COLUMN request_logs.referer IS '来源页面';
COMMENT ON COLUMN request_logs.duration IS '请求耗时(毫秒)';
COMMENT ON COLUMN request_logs.has_error IS '是否异常';
COMMENT ON COLUMN request_logs.error_message IS '异常信息';
COMMENT ON COLUMN request_logs.created_at IS '创建时间';

-- ============================================================
-- 6. 收藏夹表
-- ============================================================
CREATE TABLE IF NOT EXISTS favorite_folders (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    user_type       VARCHAR(20) NOT NULL,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(500),
    is_default      BOOLEAN DEFAULT FALSE,
    sort_order      INTEGER DEFAULT 0,
    item_count      BIGINT DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_folders_user ON favorite_folders(user_id, user_type);
CREATE INDEX IF NOT EXISTS idx_folders_default ON favorite_folders(user_id, user_type, is_default);

COMMENT ON TABLE favorite_folders IS '收藏夹表';
COMMENT ON COLUMN favorite_folders.id IS '收藏夹ID（雪花算法生成）';
COMMENT ON COLUMN favorite_folders.user_id IS '用户ID';
COMMENT ON COLUMN favorite_folders.user_type IS '用户类型(AGENT/USER)';
COMMENT ON COLUMN favorite_folders.name IS '收藏夹名称';
COMMENT ON COLUMN favorite_folders.description IS '收藏夹描述';
COMMENT ON COLUMN favorite_folders.is_default IS '是否默认收藏夹';
COMMENT ON COLUMN favorite_folders.sort_order IS '排序序号';
COMMENT ON COLUMN favorite_folders.item_count IS '收藏项数量';
COMMENT ON COLUMN favorite_folders.created_at IS '创建时间';
COMMENT ON COLUMN favorite_folders.updated_at IS '更新时间';

-- ============================================================
-- 7. 收藏项表
-- ============================================================
CREATE TABLE IF NOT EXISTS favorites (
    id              BIGINT PRIMARY KEY,
    folder_id       BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    user_type       VARCHAR(20) NOT NULL,
    target_id       BIGINT NOT NULL,
    target_type     VARCHAR(20) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_user_favorite UNIQUE (user_id, user_type, target_id, target_type),
    CONSTRAINT fk_favorite_folder FOREIGN KEY (folder_id) REFERENCES favorite_folders(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_favorites_folder ON favorites(folder_id);
CREATE INDEX IF NOT EXISTS idx_favorites_target ON favorites(target_id, target_type);

COMMENT ON TABLE favorites IS '收藏项表';
COMMENT ON COLUMN favorites.id IS '收藏ID（雪花算法生成）';
COMMENT ON COLUMN favorites.folder_id IS '收藏夹ID';
COMMENT ON COLUMN favorites.user_id IS '用户ID';
COMMENT ON COLUMN favorites.user_type IS '用户类型(AGENT/USER)';
COMMENT ON COLUMN favorites.target_id IS '目标ID';
COMMENT ON COLUMN favorites.target_type IS '目标类型(agent_card)';
COMMENT ON COLUMN favorites.created_at IS '收藏时间';

-- ============================================================
-- 8. 评分表
-- ============================================================
CREATE TABLE IF NOT EXISTS scores (
    id              BIGINT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    user_name       VARCHAR(255) NOT NULL,
    user_type       VARCHAR(20) NOT NULL,
    target_id       BIGINT NOT NULL,
    target_type     VARCHAR(20) NOT NULL,
    score           INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_user_score UNIQUE (user_id, user_type, target_id, target_type)
);

CREATE INDEX IF NOT EXISTS idx_scores_target ON scores(target_id, target_type);
CREATE INDEX IF NOT EXISTS idx_scores_user ON scores(user_id, user_type);

COMMENT ON TABLE scores IS '评分表';
COMMENT ON COLUMN scores.id IS '评分ID（雪花算法生成）';
COMMENT ON COLUMN scores.user_id IS '用户ID';
COMMENT ON COLUMN scores.user_name IS '用户名';
COMMENT ON COLUMN scores.user_type IS '用户类型(AGENT/USER)';
COMMENT ON COLUMN scores.target_id IS '目标ID';
COMMENT ON COLUMN scores.target_type IS '目标类型(agent_card)';
COMMENT ON COLUMN scores.score IS '评分(1-5)';
COMMENT ON COLUMN scores.created_at IS '创建时间';
COMMENT ON COLUMN scores.updated_at IS '更新时间';

-- ============================================================
-- 初始化完成
-- ============================================================