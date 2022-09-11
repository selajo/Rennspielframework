Help()
{
	echo "Syntax: playGames.sh [Config-Dir]"
	exit $rc
}

if [ "$#" -ne 1 ]; then
    echo "Zu wenige Kommandozeilenparameter angegeben"
    Help
fi


# Rennspielframework kompilieren, testen und paketieren
mvn clean package
if [[ "$?" -ne 0 ]] ; then
  echo 'Es sind Fehler bei den Tests aufgetreten'; exit $rc
fi

# Alle Karten, die gespielt werden sollen, befinden sind in einem Directory -> einlesen
dir="$1"
for file in "$dir"/*; do
	echo Starting with $file
	mvn exec:java -Dexec.mainClass=anwendungsschicht.RennspielStart -Dexec.args="server /$file"
	echo Server terminated
done

