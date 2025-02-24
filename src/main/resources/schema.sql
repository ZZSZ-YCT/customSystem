-- 用户表，所有用户数据以 JSON 格式保存，包含昵称、权限、TOTP、密码哈希、身份等信息
CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     username TEXT UNIQUE NOT NULL,
                                     data JSON NOT NULL
);

-- Token 表，存储 refreshToken 与用户关联信息
CREATE TABLE IF NOT EXISTS tokens (
                                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      token_uuid TEXT UNIQUE NOT NULL,
                                      refresh_token TEXT NOT NULL,
                                      user_id INTEGER NOT NULL,
                                      expiry INTEGER NOT NULL,
                                      FOREIGN KEY (user_id) REFERENCES users(id)
    );

-- OAuth 应用表，存储 OAuth 应用的名称、开发者、回调地址等信息
CREATE TABLE IF NOT EXISTS oauth_apps (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          app_name TEXT NOT NULL,
                                          developer TEXT NOT NULL,
                                          callback_url TEXT NOT NULL,
                                          created_at INTEGER NOT NULL
);

-- 邀请码表（可选），用于存储生成的一次性邀请码
CREATE TABLE IF NOT EXISTS invitation_codes (
                                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                code TEXT UNIQUE NOT NULL,
                                                created_by INTEGER NOT NULL,
                                                used INTEGER DEFAULT 0,
                                                used_by INTEGER,
                                                created_at INTEGER NOT NULL,
                                                used_at INTEGER,
                                                FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (used_by) REFERENCES users(id)
    );
