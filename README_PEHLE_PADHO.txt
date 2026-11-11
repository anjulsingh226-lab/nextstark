NEXT STARK — website list GitHub se live aati hai, app update karne ki
zaroorat nahi. Steps neeche hain.

===========================================================
STEP 1 — Repo banao aur sites.json set karo
===========================================================
1. GitHub pe naya PUBLIC repository banao (naam jo chaho, e.g. "nextstark").
2. Is zip me jo "sites.json" file hai, usko us repo ke ROOT me upload karo
   (Add file > Upload files).
3. sites.json ka format:
   [
     { "name": "My Course Site", "url": "https://example.com" },
     { "name": "Practice Tests", "url": "https://example.org" }
   ]
   - "name" = button par kya dikhega
   - "url"  = tap karne par kaunsi website khulegi
   Jitni chaho utni entries add kar sakte ho, bas { } comma se alag karke.
4. File ko commit karo.
5. sites.json file ko GitHub par kholo, "Raw" button dabao, upar address bar
   se poora URL copy karo. Kuch aisa dikhega:
   https://raw.githubusercontent.com/USERNAME/REPO/main/sites.json

===========================================================
STEP 2 — App ke code me wo URL daalo
===========================================================
1. is zip ke andar:
   app/src/main/java/com/nextstark/app/SiteRepository.kt
   file kholo.
2. Is line ko dhoondo:
   const val CONFIG_URL = "https://raw.githubusercontent.com/USERNAME/REPO/main/sites.json"
3. USERNAME/REPO ki jagah apna GitHub username aur repo ka naam daal do
   (wahi URL jo Step 1.5 me copy kiya tha).
4. File save karo.

===========================================================
STEP 3 — Dusra repo banao jahan APP ka code upload hoga
===========================================================
(Ye alag repo ho sakta hai Step 1 wale se, ya wahi bhi chal jayega — jo
aasan lage.)

1. Is poore extracted folder ki saari files/folders us repo me upload karo:
   app/, build.gradle.kts, settings.gradle.kts, gradle.properties
2. ".github" folder aksar upload me skip ho jata hai, isliye alag se:
   "Add file > Create new file" me naam likho:
       .github/workflows/build.yml
   aur build.yml ka content paste karke commit karo.
3. "Actions" tab kholo, "Build APK" chalne do (3-6 min). Green tick aane par
   us run par click karke "Artifacts" me "NextStark-apk" download karo.
   Zip kholo, andar app-debug.apk milegi.
4. APK phone me bhejo aur install karo. (Debug build hai, isliye install ke
   waqt "unverified developer" warning aa sakti hai — "Install anyway"
   dabake aage badh sakte ho.)

===========================================================
Ab website add/remove kaise karein (app update NAHI karna padega)
===========================================================
1. Step 1 wale repo me sites.json file kholo.
2. Pencil (edit) dabao, entry add/remove karo, commit karo.
3. Users jab app kholenge, naya list apne aap dikhega (app ka internet
   chalu hona chahiye). Agar internet na ho to purani (cache ki hui) list
   dikhegi, naya add/remove hone par bhi app crash nahi hoga.
4. Change turant dikh jata hai, kabhi kabhi 1-2 minute tak delay ho sakta
   hai (GitHub ka caching).
