# 🧹 Cleanup Guide - Remove Legacy Files

This guide helps you clean up **legacy files** from the monolithic v1.0.0 version.

---

## ✅ What to Keep

### Essential Files (DO NOT DELETE)
```
✅ pom.xml (parent - multi-module Maven)
✅ docker-compose.yml (updated for microservices)
✅ .env.example (updated for microservices)
✅ .gitignore
✅ .github/ (CI/CD workflows)

✅ common/ (shared DTOs)
✅ service-discovery/ (Eureka)
✅ api-gateway/ (Gateway)
✅ auth-service/ (Microservice)
✅ task-service/ (Microservice)
✅ user-service/ (Microservice)
✅ frontend/ (Next.js app)

✅ k8s/ (Kubernetes manifests)
✅ README.md (main readme)
```

### Essential Documentation (DO NOT DELETE)
```
✅ DOCUMENTATION_INDEX.md (navigation)
✅ MICROSERVICES_README.md (main guide)
✅ ARCHITECTURE.md (design)
✅ DEPLOYMENT_GUIDE.md (deployment)
✅ MIGRATION_SUMMARY.md (what changed)
✅ QUICK_REFERENCE.md (quick ref)
✅ CHANGES.md (change list)
✅ CONVERSION_COMPLETE.md (summary)
```

---

## 🗑️ What to Delete

### Option A: Keep for Reference (Recommended)
Create these deprecation notices and keep the directories:

```bash
# These already created:
✅ backend/README_DEPRECATED.md
✅ docs/README_DEPRECATED.md
```

**Benefit:** Team members can still find old documentation if needed
**Action:** No deletion needed - just the deprecation notices

---

### Option B: Delete Everything Legacy

#### Delete backend/ directory
```bash
# ONLY the old backend source code
rm -rf backend/

# Keep: backend/README_DEPRECATED.md (or delete it too if you prefer)
```

**What you're deleting from backend/:**
- `backend/src/` (old monolith source code)
- `backend/target/` (old build artifacts)
- `backend/pom.xml` (old Maven file)
- `backend/Dockerfile` (old Dockerfile)
- `backend/*.iml` (old IDE files)

**What was migrated:**
- User/Auth logic → `auth-service/`
- Task logic → `task-service/`
- User admin logic → `user-service/`

#### Delete docs/ directory content
```bash
# Delete old documentation files
rm docs/API_REFERENCE.md
rm docs/DATABASE_SCHEMA.md
rm docs/DEPLOYMENT.md
rm docs/PROJECT_OVERVIEW.md
rm docs/SETUP_GUIDE.md

# Keep: docs/README_DEPRECATED.md (or delete entire docs/ directory)
rm -rf docs/

# Or keep just the deprecation notice:
# rm docs/{API_REFERENCE,DATABASE_SCHEMA,DEPLOYMENT,PROJECT_OVERVIEW,SETUP_GUIDE}.md
```

**Why delete?**
- Outdated - all info now in root directory docs
- Confusing - old API references don't match v2.0
- Cluttered - 5 files replaced by 8 better docs

---

## 📋 Step-by-Step Cleanup

### Step 1: Review (Optional)
```bash
# See what's in backend/
ls -la backend/

# See what's in docs/
ls -la docs/
```

### Step 2: Backup (Optional)
```bash
# Create backup of legacy files before deletion
tar czf legacy_backup.tar.gz backend/ docs/

# Store safely if you ever need to reference old code
```

### Step 3: Delete Backend Directory
```bash
# Delete the old monolithic backend
rm -rf backend/

# Or keep with deprecation notice:
# (Already created: backend/README_DEPRECATED.md)
```

### Step 4: Delete Docs Directory
```bash
# Option 1: Delete entirely
rm -rf docs/

# Option 2: Keep with deprecation notice
# (Already created: docs/README_DEPRECATED.md)
# Then delete the old files:
cd docs/
rm -f API_REFERENCE.md DATABASE_SCHEMA.md DEPLOYMENT.md PROJECT_OVERVIEW.md SETUP_GUIDE.md
cd ..
```

### Step 5: Verify Cleanup
```bash
# Check directory structure
tree -L 1 -d

# Should show:
.
├── auth-service/        ✅ Microservice
├── task-service/        ✅ Microservice
├── user-service/        ✅ Microservice
├── service-discovery/   ✅ Microservice
├── api-gateway/         ✅ Microservice
├── common/              ✅ Shared library
├── frontend/            ✅ Next.js app
├── k8s/                 ✅ Kubernetes
├── .github/             ✅ CI/CD
├── (NO backend/)        ✅ Deleted
├── (NO docs/)           ✅ Deleted (or cleaned)
└── *.md files in root   ✅ New docs
```

### Step 6: Update Git
```bash
# Git track the deletions
git add -A
git commit -m "chore: remove legacy monolithic backend and docs

- Remove backend/ directory (migrated to 6 microservices)
- Remove old docs/ (replaced by comprehensive root docs)
- Keep deprecation notices for reference if needed"

git push origin main
```

---

## 📊 Before vs After

### Before Cleanup
```
cloud_native_task_manager/
├── backend/ (OLD - monolithic) ← DELETE THIS
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── *.iml
│   └── target/
├── docs/ (OLD - 5 files) ← DELETE THIS
│   ├── API_REFERENCE.md
│   ├── DATABASE_SCHEMA.md
│   ├── DEPLOYMENT.md
│   ├── PROJECT_OVERVIEW.md
│   └── SETUP_GUIDE.md
├── auth-service/ ✅
├── task-service/ ✅
├── user-service/ ✅
├── api-gateway/ ✅
├── service-discovery/ ✅
├── common/ ✅
├── frontend/ ✅
├── k8s/ ✅
└── New docs in root ✅
```

### After Cleanup
```
cloud_native_task_manager/
├── auth-service/ ✅
├── task-service/ ✅
├── user-service/ ✅
├── api-gateway/ ✅
├── service-discovery/ ✅
├── common/ ✅
├── frontend/ ✅
├── k8s/ ✅
├── .github/ ✅
├── pom.xml ✅
├── docker-compose.yml ✅
├── DOCUMENTATION_INDEX.md ✅
├── MICROSERVICES_README.md ✅
├── ARCHITECTURE.md ✅
├── DEPLOYMENT_GUIDE.md ✅
├── QUICK_REFERENCE.md ✅
├── MIGRATION_SUMMARY.md ✅
└── ... (other essential files)

Total size: ↓ Smaller
Clarity: ↑ Much clearer!
Confusion: ↓ Eliminated!
```

---

## ⚙️ Automated Cleanup Script

```bash
#!/bin/bash
# cleanup_legacy.sh

echo "🧹 Cleaning up legacy files..."

# Delete backend directory
if [ -d "backend/" ]; then
    echo "Removing backend/ directory..."
    rm -rf backend/
    echo "✅ backend/ deleted"
else
    echo "⚠️ backend/ not found"
fi

# Clean docs directory
if [ -d "docs/" ]; then
    echo "Removing old docs files..."
    rm -f docs/API_REFERENCE.md
    rm -f docs/DATABASE_SCHEMA.md
    rm -f docs/DEPLOYMENT.md
    rm -f docs/PROJECT_OVERVIEW.md
    rm -f docs/SETUP_GUIDE.md
    echo "✅ Old docs files deleted"
    
    # Optional: Delete entire docs/ if empty
    # rmdir docs/
else
    echo "⚠️ docs/ not found"
fi

# Clean up old IDE files
echo "Removing old IDE files..."
find . -name "*.iml" -delete
find . -name ".idea" -type d -delete
echo "✅ IDE files cleaned"

# Check git status
echo ""
echo "📊 Git status:"
git status

echo ""
echo "✅ Cleanup complete!"
echo ""
echo "Next steps:"
echo "1. Review: git diff --name-status"
echo "2. Commit: git add -A && git commit -m 'chore: remove legacy files'"
echo "3. Push: git push"
```

**How to use:**
```bash
# Make executable
chmod +x cleanup_legacy.sh

# Run cleanup
./cleanup_legacy.sh

# Review changes
git diff --name-status

# Commit and push
git add -A
git commit -m "chore: remove legacy backend and docs directories"
git push
```

---

## ✅ Verification Checklist

After cleanup, verify:

- [ ] `backend/` directory deleted or contains only `README_DEPRECATED.md`
- [ ] `docs/` directory cleaned (or deleted entirely)
- [ ] Root `pom.xml` is parent multi-module POM
- [ ] 6 microservices directories present (auth-, task-, user-, api-, service-, common-)
- [ ] `k8s/` directory with manifests present
- [ ] `frontend/` directory present
- [ ] New documentation files in root (8 markdown files)
- [ ] `docker-compose.yml` updated for microservices
- [ ] `.env.example` updated for microservices
- [ ] `README.md` references new docs
- [ ] `.gitignore` still functional
- [ ] Git status clean after commit

---

## 🚀 After Cleanup

Once cleaned up, your project will be:

✅ **Cleaner** - No legacy code cluttering the repo  
✅ **Clearer** - New docs are in one place  
✅ **Easier** - No confusion about which version to use  
✅ **Faster** - Smaller repository size  
✅ **Better** - Ready for team collaboration  

---

## 📌 Summary

| Action | Command | Safe? |
|--------|---------|-------|
| Delete backend/ | `rm -rf backend/` | ✅ Yes (migrated) |
| Clean docs/ | `rm docs/*.md` | ✅ Yes (replaced) |
| Delete IDE files | `find . -name "*.iml" -delete` | ✅ Yes (generated) |
| Keep deprecation notices | Already done | ✅ Optional |
| Git commit | `git add -A && git commit` | ✅ Recommended |

---

**Cleanup Guide Complete**  
**Date:** March 19, 2026  
**Status:** Ready to execute

Run the commands above to clean up your repository! 🧹

