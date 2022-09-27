Untuk Jalankan Program :
- Akses halaman webhost https://www.000webhost.com/cpanel-login?from=panel
- Buat akun webhost ikut petunjuk webhost yang ada
- lalu setting url webhost kalian di menu website settings > general > website name
- klik file manager 
- klik upload files muncul tampilan untuk mengupload filesnya 
- lalu copy file api dapat diakses di folder heartbeat:
  heartbeat>backend
- buka halaman upload files tadi lalu paste file api tersebut di folder public_html
- buat database baru pada halaman webhosting pilih menu database->mysql database 
- selanjutnya import file database (.sql) yang dapat diakses pada folder heartbeat:
  heartbeat>backend
- kembali lagi ke halaman public_html tadi masuk ke folder api lalu pilih koneksi.php 
- ubah user id, password, dan nama database
- lalu masuk ke folder heartbeat>app>src>main>java>eu>berdosi>app>heartbeat>api
- buka file Apiclient.java 
- ubah base_url menjadi url webhost kalian yang telah kalian setting sebelumnya
- program sudah dapat dijalankan.


