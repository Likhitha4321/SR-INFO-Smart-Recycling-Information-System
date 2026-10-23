package com.example.my;


import java.util.ArrayList;


public class EcoQuestionModel {


    int id;

    String category;

    String difficulty;

    String question;

    ArrayList<String> options;

    int correctAnswer;

    int points;

    String explanation;

    String funFact;

    String ecoBuddySpeech;



    public EcoQuestionModel(

            int id,

            String category,

            String difficulty,

            String question,

            ArrayList<String> options,

            int correctAnswer,

            int points,

            String explanation,

            String funFact,

            String ecoBuddySpeech

    ){


        this.id = id;

        this.category = category;

        this.difficulty = difficulty;

        this.question = question;

        this.options = options;

        this.correctAnswer = correctAnswer;

        this.points = points;

        this.explanation = explanation;

        this.funFact = funFact;

        this.ecoBuddySpeech = ecoBuddySpeech;


    }



}
