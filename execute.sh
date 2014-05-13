function normal
{
    java -cp .:bin pack.Timers 2> /dev/null
}

function weekly
{
    local v
    v=`date "+%w"`
    if [ v = "0" ] || [ v = "6" ]; then
        java -cp .:bin pack.Timers "data/weekend.txt" 2> /dev/null
    else
        java -cp .:bin pack.Timers "data/title.txt" 2> /dev/null
    fi
}

weekly
