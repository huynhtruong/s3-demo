Install ImagemagicK 
sudo apt install imagemagick

Solved this problem by checking the real java.library.path with:

sudo apt-get install -y jmagick

ln -s /usr/lib/jni/libJMagick.so /usr/lib/libJMagick.so