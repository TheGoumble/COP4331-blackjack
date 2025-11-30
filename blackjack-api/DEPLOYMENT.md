# Deploying to Google Cloud Run

## Prerequisites

1. **Google Cloud Account**

2. **Install Google Cloud CLI**
   - Download: https://cloud.google.com/sdk/docs/install
   - Or use PowerShell:
   ```powershell
   (New-Object Net.WebClient).DownloadFile("https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe", "$env:Temp\GoogleCloudSDKInstaller.exe")
   & $env:Temp\GoogleCloudSDKInstaller.exe
   ```

## Step-by-Step Deployment

### 1. Initialize Google Cloud

```powershell
# Login to Google Cloud
gcloud auth login

# Create a new project (or use existing)
gcloud projects create blackjack-matchmaking --name="Blackjack Matchmaking"

# Set active project
gcloud config set project blackjack-matchmaking

# Enable Cloud Run API
gcloud services enable run.googleapis.com
gcloud services enable cloudbuild.googleapis.com
```

### 2. Deploy from Source

Navigate to the API directory:
```powershell
cd blackjack-api
```

Deploy to Cloud Run:
```powershell
gcloud run deploy blackjack-api `
  --source . `
  --platform managed `
  --region us-central1 `
  --allow-unauthenticated `
  --memory 512Mi `
  --cpu 1 `
  --min-instances 0 `
  --max-instances 10
```

**What this does:**
- `--source .` - Deploys from current directory (auto-detects Dockerfile)
- `--allow-unauthenticated` - Public API (anyone can call it)
- `--min-instances 0` - Scales to zero when not used = FREE
- `--max-instances 10` - Handles traffic spikes

### 3. Get Your API URL

After deployment completes, you'll see:
```
Service URL: https://blackjack-api-xxxxx-uc.a.run.app
```

**Copy this URL!** You'll need it for the Java client.

### 4. Test Your Deployed API

```powershell
# Replace with your actual URL
$API_URL = "https://blackjack-api-xxxxx-uc.a.run.app"

# Test health endpoint
irm "$API_URL/health"

# Register a test game
irm "$API_URL/api/games/register" -Method Post -ContentType "application/json" -Body '{"gameCode":"TEST01","hostId":"player1","address":"192.168.1.100","port":12345}'

# List games
irm "$API_URL/api/games/list"
```

### 5. Update Java Client

Edit `ApiClient.java`:
```java
private static final String DEFAULT_API_URL = "https://blackjack-api-xxxxx-uc.a.run.app/api";
```

Recompile:
```powershell
mvn clean compile
```

### 6. Monitor & Manage

**View logs:**
```powershell
gcloud run services logs read blackjack-api --region us-central1 --limit 50
```

**View metrics:**
```powershell
gcloud run services describe blackjack-api --region us-central1
```

**Update deployment:**
```powershell
# After making changes to server.js
gcloud run deploy blackjack-api --source . --region us-central1
```

**Delete service:**
```powershell
gcloud run services delete blackjack-api --region us-central1
```
