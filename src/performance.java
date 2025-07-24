package com.academic.examapp.model;
public class performance {
   private int studentId;
   private int questionId;
   private boolean isCorrect;
   public performance(int studentId, int questionId, boolean isCorrect) {
       this.studentId = studentId;
       this.questionId = questionId;
       this.isCorrect = isCorrect;
   }
   public int getStudentId() { return studentId; }
   public int getQuestionId() { return questionId; }
   public boolean isCorrect() { return isCorrect; }
}
