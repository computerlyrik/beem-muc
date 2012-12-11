INTRODUCTION
------------
asmack http://code.google.com/p/asmack/ is a portage of the Smack library for
the Android platform. The project is mostly maintained by Rene Treffer and you
can find the binaries at http://code.google.com/p/asmack/ and the sources at
http://github.com/rtreffer/asmack

The asmack project is based on the development version of the Smack library.
This version is constantly moving but we want a fixed version for BEEM.
We patched the asmack build process to use a fixed version of the Smack
library. Currently, we use the revision 11644 of the Smack SVN repository.

COMPILE
-------

First check out the last version of asmack
> git clone git://github.com/rtreffer/asmack.git

Then apply the beem-build-process.patch on the source.
> cd asmack
> patch -p1 < beem-build-process.patch
>

Add the beem flavour to the patch repository
> cp -R beem_patches patch/beem
>

The 50-fix_chatmanager.patch is only necessary to fix a little bug in smack. The
patch has been proposed to the Smack developers. See
http://www.igniterealtime.org/issues/browse/SMACK-269 for progress.

Edit your local.properties file to contains the path of the android SDK. See
local.properties.example

Build asmack
> ./build.batch
>

The build directory will contains the files :
 * asmack-android-$VERSION-beem.jar for asmack binaries for android $VERSION
 * asmack-android-$VERSION-source-beem.zip for the asmack custom sources for
   android $VERSION.

