# Deploying to Google Cloud Run

## Prerequisites

1. **Google Cloud Account** - Free tier includes:
   - 2 million requests/month
   - 360,000 GB-seconds
   - $300 free credit for new users

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

## Cost Breakdown (Free Tier)

**Your Expected Usage:**
- ~10 games/day = 300 games/month
- ~5 API calls per game = 1,500 requests/month
- **Cost: $0.00** (well under 2M request limit)

**What uses quota:**
- Game registration: 1 request
- Lobby refresh: 1 request each
- Join/leave: 1 request each

**Free tier covers:**
- Up to 2 million requests/month
- Up to 360,000 GB-seconds compute time
- 1 GB network egress

## Troubleshooting

**Error: "Project not found"**
```powershell
gcloud config set project blackjack-matchmaking
```

**Error: "Permission denied"**
```powershell
gcloud auth login
gcloud auth application-default login
```

**Error: "Service not found"**
- Check region: `gcloud run services list`
- Deployment may have failed - check: `gcloud run services describe blackjack-api --region us-central1`

**API returns 404:**
- Check URL format: `https://xxx.run.app/api/games/list` (note `/api` prefix)
- View logs: `gcloud run services logs read blackjack-api --region us-central1`

## Alternative: Quick Deploy Button

1. Go to https://console.cloud.google.com/run
2. Click "Create Service"
3. Select "Deploy from source"
4. Connect to your GitHub repo
5. Set source location: `/blackjack-api`
6. Click "Deploy"

## Environment Variables (Optional)

To add custom config:
```powershell
gcloud run deploy blackjack-api `
  --source . `
  --region us-central1 `
  --set-env-vars MAX_PLAYERS=4,CLEANUP_INTERVAL=600000
```

Update `server.js` to read:
```javascript
const MAX_PLAYERS = process.env.MAX_PLAYERS || 4;
const CLEANUP_INTERVAL = process.env.CLEANUP_INTERVAL || (10 * 60 * 1000);
```

## Custom Domain (Optional)

If you own a domain:
```powershell
gcloud run domain-mappings create --service blackjack-api --domain api.yourdomain.com --region us-central1
```

Then update DNS:
```
Type: CNAME
Name: api
Value: ghs.googlehosted.com
```

## Production Checklist

- [ ] API deployed and responding
- [ ] Health endpoint returns 200
- [ ] Can register/list/join/leave games
- [ ] Java client updated with production URL
- [ ] Tested with 2+ clients
- [ ] Logs show correct behavior
- [ ] Set billing alerts (optional)

## Need Help?

- Cloud Run Docs: https://cloud.google.com/run/docs
- Pricing Calculator: https://cloud.google.com/products/calculator
- Support: https://cloud.google.com/support
