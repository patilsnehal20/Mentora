package com.academic.examapp.model;
public class Question {
   private int questionId;
   private String topic;
   private String subTopic;
   private int difficultyLevel;
   private int marks;
   private String questionText;
   private String correctOption;
   private String optionA, optionB, optionC, optionD;
   private String questionType;
   private String answer;
   // Constructor
   public Question(int questionId, String topic, String subTopic, int difficultyLevel, int marks,
                   String questionText, String correctOption,
                   String optionA, String optionB, String optionC, String optionD,
                   String answer,String questionType) {
       this.questionId = questionId;
       this.topic = topic;
       this.subTopic = subTopic;
       this.difficultyLevel = difficultyLevel;
       this.marks = marks;
       this.questionText = questionText;
       this.correctOption = correctOption;
       this.optionA = optionA;
       this.optionB = optionB;
       this.optionC = optionC;
       this.optionD = optionD;
       this.answer = answer;
       this.questionType = (questionType == null || questionType.isEmpty()) ? "MCQ" : questionType;

       
   }
   // Getter methods
   public int getQuestionId() {
       return questionId;
   }
   public String getTopic() {
       return topic;
   }
   public String getSubTopic() {
       return subTopic;
   }
   public int getDifficultyLevel() {
       return difficultyLevel;
   }
   public int getMarks() {
       return marks;
   }
   public String getQuestionText() {
       return questionText;
   }
   public String getCorrectOption() {
       return correctOption;
   }
   public String getOptionA() {
       return optionA;
   }
   public String getOptionB() {
       return optionB;
   }
   public String getOptionC() {
       return optionC;
   }
   public String getOptionD() {
       return optionD;
   }
   public String getAnswer() {
       return answer;
   }
   public String getQuestionType() {
       return questionType;
   }
}

