# Include in BoardConfig.mk
# SamsungParts service definitions

BOARD_VENDOR_SEPOLICY_DIRS += \
    hardware/samsung-ext/packages/apps/SamsungParts/sepolicy/vendor

SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += \
    hardware/samsung-ext/packages/apps/SamsungParts/sepolicy/private

SYSTEM_EXT_PUBLIC_SEPOLICY_DIRS += \
    hardware/samsung-ext/packages/apps/SamsungParts/sepolicy/public
