
# compile 
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" cardsMain

# run using this
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" cardsMain

# to run on powershell
chcp 65001
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" cardsMain