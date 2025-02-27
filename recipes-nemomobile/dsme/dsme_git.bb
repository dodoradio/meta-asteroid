SUMMARY = "Nemomobile's DSME"
HOMEPAGE = "https://github.com/sailfishos/dsme"
LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

SRC_URI = "gitsm://github.com/sailfishos/dsme.git;protocol=https \
    file://dsme.service"
SRCREV = "d1518176a68ce416fd19515c0b88da2b48ce606a"
PR = "r1"
PV = "+git${SRCPV}"
S = "${WORKDIR}/git"

DEPENDS += "qtbase libdsme glib-2.0 libngf libiphb systemd dbus dbus-glib mce cryptsetup"
inherit autotools pkgconfig

B = "${WORKDIR}/git"
# Poweron-timer needs libcal but I can't find it
EXTRA_OECONF= " --disable-poweron-timer --disable-upstart --enable-systemd --enable-runlevel --enable-pwrkeymonitor --disable-validatorlistener --disable-static --enable-usewheel"

do_configure:prepend() {
    sed -i "s@<policy user=\"root\">@<policy user=\"ceres\">@" dsme/dsme.conf
    sed -i "s@-L/lib -lsystemd-daemon@-lsystemd@" dsme/Makefile.am
    sed -i "s@LDFLAGS \= \-pthread@LDFLAGS \= \-L${STAGING_DIR_TARGET}/usr/lib \-pthread@" modules/Makefile.am
}

do_compile() {
    find . -type f -print0 | xargs -0 sed -i "s/\-Werror//"
    oe_runmake V=1
}

do_install:append() {
    install -D -m 644 reboot-via-dsme.sh ${D}/etc/profile.d/reboot-via-dsme.sh
    install -D -m 644 ../dsme.service ${D}/lib/systemd/system/dsme.service
    install -d ${D}/lib/systemd/system/multi-user.target.wants/
    ln -s ../dsme.service ${D}/lib/systemd/system/multi-user.target.wants/dsme.service
    install -d ${D}/var/lib/dsme
    [ ! -f ${D}/var/lib/dsme/alarm_queue_status ] && echo 0 > ${D}/var/lib/dsme/alarm_queue_status
}

FILES:${PN} += "/lib/systemd/"
FILES:${PN}-dbg += "/opt"
