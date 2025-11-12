# Firestore Billing Setup Guide

## Issue
You're seeing this error in your logs:
```
Status{code=PERMISSION_DENIED, description=This API method requires billing to be enabled. 
Please enable billing on project weflut-live by visiting 
https://console.developers.google.com/billing/enable?project=weflut-live
```

## Why This Happens
Firestore real-time listeners (like `addSnapshotListener`) require billing to be enabled on your Firebase project, even for the free tier (Spark plan).

## Solution: Enable Billing (Free Tier)

### Step 1: Go to Firebase Console
1. Visit: https://console.firebase.google.com/
2. Select your project: **weflut-live** (NewsAggregator)

### Step 2: Enable Billing
1. Click on the **⚙️ Settings** (gear icon) in the left sidebar
2. Select **Project settings**
3. Go to the **Usage and billing** tab
4. Click **Modify plan** or **Upgrade project**
5. Select the **Spark Plan** (FREE tier)
   - This gives you:
     - 1 GB storage
     - 10 GB/month network egress
     - 50K reads/day
     - 20K writes/day
     - 20K deletes/day
6. Add a payment method (required even for free tier, but you won't be charged unless you exceed limits)
7. Complete the setup

### Step 3: Wait for Propagation
- After enabling billing, wait **2-5 minutes** for changes to propagate
- Restart your app

### Step 4: Verify
- Check your logs - the PERMISSION_DENIED error should disappear
- Test bookmarking articles - they should save in real-time

## Alternative: Use One-Time Reads (No Billing Required)
If you don't want to enable billing, you can modify the code to use `.get().await()` instead of `.addSnapshotListener()`, but you'll lose real-time updates.

## Current Implementation
The app uses real-time listeners in:
- `NewsRepositoryImpl.getBookmarkedArticles()` - Uses `addSnapshotListener` for real-time bookmark updates
- This requires billing to be enabled

## Free Tier Limits (Spark Plan)
- **Storage**: 1 GB
- **Network**: 10 GB/month
- **Reads**: 50,000/day
- **Writes**: 20,000/day
- **Deletes**: 20,000/day

For a news aggregator app, these limits are usually sufficient unless you have thousands of active users.

