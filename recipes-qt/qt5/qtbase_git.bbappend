FILESEXTRAPATHS_prepend := "${THISDIR}/qtbase:"
SRC_URI += " file://0001-Forces-GLES2-the-dirty-way.patch"

# Remove dependencies to mesa
PACKAGECONFIG_remove = "kms"
PACKAGECONFIG_remove = "widgets"
PACKAGECONFIG_remove = "gl"
PACKAGECONFIG_append += "gles2 mtdev sql-sqlite glib"

QT_CONFIG_FLAGS += "-no-qpa-platform-guard"
PACKAGECONFIG_X11 = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xcb xrender xinput2 glib xkb xkbcommon-evdev', 'xkbcommon-evdev', d)}"
