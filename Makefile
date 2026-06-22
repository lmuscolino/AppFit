# ============================================================
#  AppFit — Makefile per la compilazione dell'APK
#  Uso: make          → scarica SDK e compila APK debug
#       make release  → compila APK release (non firmato)
#       make clean    → pulisce i file di build
#       make install  → installa APK su dispositivo/emulatore collegato
#       make sdk      → scarica e configura solo l'Android SDK
# ============================================================

# ---- Configurazione ----------------------------------------
JAVA_HOME        := /home/lorenzo/java17
ANDROID_SDK_DIR  := $(HOME)/android-sdk
CMDLINE_TOOLS_VER := 11076708
CMDLINE_TOOLS_URL := https://dl.google.com/android/repository/commandlinetools-linux-$(CMDLINE_TOOLS_VER)_latest.zip
GRADLE_WRAPPER_JAR_URL := https://github.com/gradle/gradle/raw/v8.6.0/gradle/wrapper/gradle-wrapper.jar

BUILD_TOOLS_VER  := 34.0.0
PLATFORM_VER     := android-34

APK_DEBUG        := app/build/outputs/apk/debug/app-debug.apk
APK_RELEASE      := app/build/outputs/apk/release/app-release-unsigned.apk

SDKMANAGER       := $(ANDROID_SDK_DIR)/cmdline-tools/latest/bin/sdkmanager
GRADLEW          := ./gradlew

# ---- Export variabili d'ambiente ---------------------------
export JAVA_HOME
export ANDROID_HOME := $(ANDROID_SDK_DIR)
export ANDROID_SDK_ROOT := $(ANDROID_SDK_DIR)
export PATH := $(JAVA_HOME)/bin:$(ANDROID_SDK_DIR)/platform-tools:$(ANDROID_SDK_DIR)/cmdline-tools/latest/bin:$(PATH)

# ============================================================
.PHONY: all debug release clean install sdk test help

all: debug

# ---- Target principale: APK debug --------------------------
debug: sdk gradle-wrapper local.properties
	@echo ""
	@echo ">>> Compilazione APK debug..."
	$(GRADLEW) assembleDebug --stacktrace
	@echo ""
	@echo "✓ APK generato: $(APK_DEBUG)"
	@ls -lh $(APK_DEBUG)

# ---- APK release (non firmato) ----------------------------
release: sdk gradle-wrapper local.properties
	@echo ""
	@echo ">>> Compilazione APK release (non firmato)..."
	$(GRADLEW) assembleRelease --stacktrace
	@echo ""
	@echo "✓ APK release: $(APK_RELEASE)"
	@ls -lh $(APK_RELEASE)

# ---- Scarica e configura Android SDK ----------------------
sdk: $(SDKMANAGER)

$(SDKMANAGER):
	@echo ""
	@echo ">>> Android SDK non trovato. Download command-line tools..."
	mkdir -p $(ANDROID_SDK_DIR)/cmdline-tools
	cd /tmp && \
		wget -q --show-progress -O cmdline-tools.zip "$(CMDLINE_TOOLS_URL)" && \
		unzip -q -o cmdline-tools.zip -d $(ANDROID_SDK_DIR)/cmdline-tools && \
		mv $(ANDROID_SDK_DIR)/cmdline-tools/cmdline-tools $(ANDROID_SDK_DIR)/cmdline-tools/latest && \
		rm cmdline-tools.zip
	@echo ""
	@echo ">>> Accettazione licenze SDK..."
	yes | $(SDKMANAGER) --licenses > /dev/null 2>&1 || true
	@echo ""
	@echo ">>> Installazione pacchetti SDK necessari..."
	$(SDKMANAGER) \
		"platform-tools" \
		"platforms;$(PLATFORM_VER)" \
		"build-tools;$(BUILD_TOOLS_VER)"
	@echo "✓ Android SDK configurato in $(ANDROID_SDK_DIR)"

# ---- Scarica gradle-wrapper.jar se mancante ---------------
gradle-wrapper: gradle/wrapper/gradle-wrapper.jar

gradle/wrapper/gradle-wrapper.jar:
	@echo ""
	@echo ">>> Download gradle-wrapper.jar..."
	wget -q --show-progress -O gradle/wrapper/gradle-wrapper.jar \
		"$(GRADLE_WRAPPER_JAR_URL)"
	@echo "✓ gradle-wrapper.jar scaricato"

# ---- Crea local.properties --------------------------------
local.properties:
	@echo ">>> Creazione local.properties..."
	@printf "sdk.dir=$(ANDROID_SDK_DIR)\norg.gradle.java.home=$(JAVA_HOME)\n" > local.properties
	@echo "✓ local.properties creato"

# ---- Installa su dispositivo ------------------------------
install: debug
	@echo ""
	@echo ">>> Installazione APK su dispositivo..."
	$(ANDROID_SDK_DIR)/platform-tools/adb install -r $(APK_DEBUG)

# ---- Test JUnit (JVM, senza device) ----------------------
test: local.properties gradle-wrapper
	@echo ""
	@echo ">>> Esecuzione test JUnit..."
	$(GRADLEW) :app:testDebugUnitTest
	@echo ""
	@echo "✓ Report: app/build/reports/tests/testDebugUnitTest/index.html"

# ---- Pulisci build ----------------------------------------
clean:
	@echo ">>> Pulizia build..."
	$(GRADLEW) clean
	rm -f local.properties
	@echo "✓ Build pulita"

# ---- Pulisci tutto compreso SDK ---------------------------
clean-all: clean
	@echo ">>> Rimozione Android SDK ($(ANDROID_SDK_DIR))..."
	rm -rf $(ANDROID_SDK_DIR)
	rm -f gradle/wrapper/gradle-wrapper.jar
	@echo "✓ Tutto rimosso"

# ---- Help -------------------------------------------------
help:
	@echo ""
	@echo "AppFit — Comandi disponibili:"
	@echo "  make          → Compila APK debug (scarica SDK se necessario)"
	@echo "  make release  → Compila APK release non firmato"
	@echo "  make install  → Installa APK debug su dispositivo ADB"
	@echo "  make sdk      → Scarica e configura solo l'Android SDK"
	@echo "  make test     → Esegue i test JUnit (senza device, veloce)"
	@echo "  make clean    → Pulisce i file di build"
	@echo "  make clean-all → Rimuove build + Android SDK"
	@echo ""
	@echo "APK output: $(APK_DEBUG)"
	@echo "JAVA_HOME:  $(JAVA_HOME)"
	@echo "ANDROID_HOME: $(ANDROID_SDK_DIR)"
	@echo ""
