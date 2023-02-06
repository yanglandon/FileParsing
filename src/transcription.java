import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
public class transcription {
    public static void main(String[] args) throws IOException {
        String fileContents = readFile("sample01.vtt");
        String[] data = fileContents.split("\n");
        extraction(data);
    }
    public static void extraction(String[] data){
        ArrayList<Double> speakingDurations = new ArrayList<>();
        ArrayList<String> speakers = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i].contains(">") && data[i + 1].contains(":")){
                speakingDurations.add(typeTime(data[i]));
            } else if (data[i].contains(":") && !data[i].contains(">")){
// bc else if need to not be a time type line, and no other lines have a colon
                speakers.add(typeSpeak(data[i]));
            }
        }
        toCondensedTranscript(speakingDurations, speakers);
        toSummaryFile(speakingDurations, speakers);
    }
    private static void toSummaryFile(ArrayList<Double> speakingDurations, ArrayList<String>
            speakers) {
        double totalTime = length(speakingDurations);
        double switches = switches(speakers);
        ArrayList<Speaker> speakerList = getSpeakerVals(speakers, speakingDurations);
        writeDataToFile("SummaryFile", fileData(speakerList, totalTime, switches));
    }
    private static String fileData(ArrayList<Speaker> speakerList, double totalTime, double switches)
    {
        String toFile = "Summary statistics file: \n\n";
        toFile += "Total # people: " + speakerList.size() + "\nTotal length of session: " + totalTime + " seconds" +
                "\nTotal # of speaker switches: " + switches;
        toFile += "\n\nTotal talk time \n";
        for (Speaker speaker: speakerList) {
            toFile += speaker.getName() + ": " + speaker.getTimeSpoken() + " min \n";
        }
        toFile += "\n\nAverage speaking time \n";
        for (Speaker speaker: speakerList) {
            toFile += speaker.getName() + ": " + speaker.getAvgSpokenTime() + " min \n";
        }
        return toFile;
    }
    private static ArrayList<Speaker> getSpeakerVals(ArrayList<String> speakers,
                                                     ArrayList<Double> speakingDurations) {
        ArrayList<String> uniqueSpeakers = onlyUniqueSpeakers(speakers);
        ArrayList<Speaker> speakingVals = new ArrayList<>();
        for (int i = 0; i < uniqueSpeakers.size(); i++) {
            speakingVals.add(new Speaker(uniqueSpeakers.get(i),0,0,0 ));
        }
        speakingVals = setSpeakerVals(speakers, speakingDurations, speakingVals);
        return speakingVals;
    }
    public static ArrayList<Speaker> setSpeakerVals(ArrayList<String> allSpeakers,
                                                    ArrayList<Double> speakingDurations, ArrayList<Speaker> speakers){
        for (int i = 0; i < speakingDurations.size(); i++) {
            for (int j = 0; j < speakers.size(); j++) {
                if (allSpeakers.get(i).equals(speakers.get(j).getName())){
                    speakers.get(j).incrementTimeSpoken(speakingDurations.get(i));
                }
            }
        }
        speakers = setAvgTimes(speakers);
        return speakers;
    }
    private static ArrayList<Speaker> setAvgTimes(ArrayList<Speaker> speakers) {
        for (int i = 0; i < speakers.size(); i++) {
            speakers.get(i).setAvgSpokenTime();
        }
        return speakers;
    }
    private static ArrayList<String> onlyUniqueSpeakers(ArrayList<String> speakers) {
        ArrayList<String> uniqueSpeakers = new ArrayList<>();
        for (String speaker: speakers) {
            if (!isDuplicate(speaker, uniqueSpeakers)){
                uniqueSpeakers.add(speaker);
            }
        }
        return uniqueSpeakers;
    }
    public static boolean isDuplicate(String currName, ArrayList<String> namesList){
        boolean duplicate = false;
        for (String name : namesList) {
            if (currName.equals(name)) {
                return true;
            }
        }
        return duplicate;
    }
    private static double length(ArrayList<Double> speakingDurations) {
        double totalTime = 0;
        for (double spokenTime: speakingDurations) {
            totalTime += spokenTime;
        }
        return totalTime;
    }
    private static int switches(ArrayList<String> speakers) {String speaker = "";
        int change = 0;
        for (int i = 0; i < speakers.size() - 1; i++){
            speaker = speakers.get(i);
            if (!speaker.equals(speakers.get(i + 1))){
                change++;
            }
        }
        return change;
    }
    private static void toCondensedTranscript(ArrayList<Double> speakingDurations,
                                              ArrayList<String> speakers) {
        String line = "Condensed transcript file: \n\n";
        for (int i = 0; i < speakingDurations.size(); i++) {
            line += (speakers.get(i) + ": " + speakingDurations.get(i) + " sec \n");
        }
        writeDataToFile("CondensedFile", line);
    }
    private static double typeTime(String speakingTime) {
        ArrayList<Double> speakingDuration = new ArrayList<>();
        String [] singleTimeStamps = speakingTime.split(" --> ");
        for (int i = 0; i < singleTimeStamps.length; i++) {
            speakingDuration.add(singleDuration(singleTimeStamps[i]));
        }
        return difference(speakingDuration);
    }
    public static double difference(ArrayList<Double> timeStampsInSec){
        return timeStampsInSec.get(1) - timeStampsInSec.get(0);
    }
    public static double singleDuration(String timeStamp){
        String[] singlePart = timeStamp.split(":");
        double timeInSec = 0;
        for (int i = 0; i < singlePart.length; i++) {
            timeInSec += convertToSeconds(singlePart[i], i);
        }
        return timeInSec;
    }
    private static Double convertToSeconds(String value, int timeUnit) {
        double duration = Double.parseDouble(value);
        if (timeUnit == 0){
            duration *= Math.pow(60, 2);
        } else if (timeUnit == 1){
            duration *= 60;
        }
        return duration;
    }
    private static String typeSpeak(String speak) {
        int ind = speak.indexOf(":");
        return speak.substring(0, ind);
    }
    public static void writeDataToFile(String filePath, String data) {
        try (FileWriter f = new FileWriter(filePath);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter writer = new PrintWriter(b);) {
            writer.println(data);
        } catch (IOException error) {
            System.err.println("There was a problem writing to the file: " + filePath);
            error.printStackTrace();
        }
    }
    public static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}