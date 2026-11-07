# Terms of Service & Privacy Policy (TAP)

**Dquests** (by MinhSangGD)  
Last Updated: 07 November, 2026

---

## 1. Terms of Service

### 1.1 Acceptance of Terms
By downloading, installing, accessing, or using **Dquests** (the "Application"), you acknowledge that you have read, understood, and agreed to be bound by these Terms of Service. If you do not agree to these terms, do not install or use the Application.

### 1.2 Nature of the Software
Dquests is an independent client-side automation utility designed to interact with Discord API endpoints directly from your personal device for personal productivity, task automation, and Rich Presence customization.

### 1.3 Disclaimer of Affiliation
**Dquests is NOT affiliated with, authorized, maintained, sponsored, or endorsed by Discord Inc. or any of its affiliates or subsidiaries.**  
"Discord" and its respective logos and trademarks are the registered property of Discord Inc.

### 1.4 User Responsibilities & Account Risks
- **Third-Party Terms Violation:** Automating user actions or spoofing desktop client behaviors may violate Discord's Terms of Service and Developer Policy.
- **Assumption of Risk:** You explicitly understand and agree that any automation activity carries an inherent risk of rate limiting, account warnings, feature suspension, or account termination by Discord Inc.
- **Zero Liability:** The author and contributors of Dquests assume **no responsibility or liability** for any consequence resulting from your use of this tool, including but not limited to account suspensions, loss of access, or service interruptions. You use this Application entirely at your own discretion and risk.

---

## 2. Privacy Policy

### 2.1 100% Local Execution
Dquests is built on a strict **zero-telemetry, client-only architecture**:
- **Direct Communication:** Every network request made by the Application communicates directly and exclusively with official Discord servers (`discord.com`, `gateway.discord.gg`, and Discord CDNs) or directly checks public version tags hosted on the author’s GitHub repository.
- **No Intermediary Servers:** The author operates no backend servers, proxy relays, data collection endpoints, analytics hubs, or telemetry brokers.

### 2.2 Credential & Token Handling
- **Local Storage Only:** Your login credentials (Email, Password, Authorization Tokens, Multi-Factor Authentication tickets) and profile data are stored exclusively on your device's local sandboxed storage via Android `SharedPreferences`.
- **No Third-Party Transmission:** Your credentials, tokens, and active sessions are **never** transmitted, mirrored, logged, or shared with any third party, including the developer.

### 2.3 Permissions Used
The Application requests only the minimal platform permissions strictly necessary for its local operation:
- `android.permission.INTERNET`: Required to communicate directly with Discord's public APIs and Gateway.
- `android.permission.WAKE_LOCK`: Prevents CPU sleep states during long-running background tasks when screen retention is active.
- `android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: Prompts the operating system Doze Mode to prevent unexpected task termination while background farming.
- `android.permission.QUERY_ALL_PACKAGES`: Used exclusively to list locally installed applications so you can pick games/apps for Custom Rich Presence (RPC) setup. No application inventory data is collected or sent anywhere.

---

## 3. Contact & Open Source

This project is created and maintained by **MinhSangGD**.  
Official Repository: [https://github.com/MinhSangGDVN/dquests](https://github.com/MinhSangGDVN/dquests)

