# 参与贡献

感谢你愿意改进轻账。为了让问题容易复现、代码容易审查，请先阅读下面的约定。

## 开始之前

- 普通 Bug 和功能建议请使用仓库提供的 Issue 表单。
- 安全漏洞不要发布到公开 Issue，请按照 [SECURITY.md](SECURITY.md) 报告。
- 提交代码即表示你同意遵守 [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)。
- 当前仓库尚未选择开源许可证；在 `LICENSE` 文件加入前，请不要复制、重新发布或用于其他项目。

## 本地开发

需要 Java 17 或更高版本，以及 Maven 3.6 或更高版本。

```powershell
git clone https://github.com/xyl0619/Expense.git
Set-Location Expense
mvn spring-boot:run
```

默认使用项目目录中的 H2 文件数据库。需要验证 MySQL 环境时，复制 `.env.example` 为 `.env`，替换全部占位值，然后运行：

```powershell
docker compose up --build
```

不要提交 `.env`、数据库文件、日志、令牌、真实邮箱或密码。

## 推荐流程

1. 先搜索现有 Issue，确认问题没有重复。
2. Fork 仓库，从最新 `main` 创建一个范围明确的分支。
3. 只修改与当前问题有关的代码和文档。
4. 为行为变化补充或更新测试。
5. 在本地运行 `mvn verify`。
6. 提交 Pull Request，并完整填写模板。

建议使用简短、清楚的提交信息：

```text
feat: add recurring budget support
fix: reject invalid expense date range
docs: clarify Docker setup
test: cover administrator deletion guard
```

## 代码约定

- Controller 负责协议转换，业务与权限规则放在 Service 层。
- 不信任浏览器传入的用户 ID；数据归属必须由后端当前身份决定。
- API 输入使用 DTO 和 Bean Validation，不直接接收实体对象。
- 新增数据库结构时添加新的 Flyway 迁移，不修改已经发布的迁移文件。
- 新增界面文字时，同时补充 `static/js/i18n.js` 中的中文和英文内容。
- 不添加无用途的依赖、重复工具类或与当前改动无关的重构。

## 测试要求

Pull Request 至少应通过：

```powershell
mvn verify
```

如果修改前端脚本，也建议运行：

```powershell
node --check src/main/resources/static/js/i18n.js
node --check src/main/resources/static/js/common.js
node --check src/main/resources/static/js/dashboard.js
node --check src/main/resources/static/js/admin.js
```

本机没有 Docker 时，Testcontainers 的 MySQL 测试可能跳过；GitHub Actions 会在 Pull Request 中运行完整验证。

## Pull Request 审查标准

维护者会重点检查：

- 改动是否解决一个明确问题；
- 普通用户、管理员和数据归属是否仍然安全；
- H2 与 MySQL 两种运行方式是否兼容；
- 中英文界面是否同步；
- 测试、文档和数据库迁移是否与代码一致；
- 是否包含秘密、个人数据、生成文件或无关改动。

