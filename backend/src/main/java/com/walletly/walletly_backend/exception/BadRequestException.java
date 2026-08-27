package com.walletly.walletly_backend.exception; 

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) { /// Ici on définit message en String en paramètre pour pouvoir envoyer un message lorsqu'il y a une erreur exemple : "A category is required"
        super(message); /// super() = appelle le constructeur de la classe parent donc RuntimeException. 
    }

}