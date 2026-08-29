package com.walletly.walletly_backend.exception; 

public class ErrorMessages {
    
    private ErrorMessages() {

    }

    // ERROR EXPENSE 
    public static final String EXPENSE_NOT_FOUND = "Expense not found"; 
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String INVALID_EXPENSE_AMOUNT = "Amount must be positive";
    public static final String EXPENSE_DESCRIPTION_REQUIRED = "Description is required";
    public static final String EXPENSE_DESCRIPTION_LENGTH = "Description must be field";
    public static final String EXPENSE_DATE_REQUIRED = "Date is required"; 
    public static final String EXPENSE_DATE_IN_FUTURE = "Date cannot be in the future";


    // ERROR CATEGORY 
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CATEGORY_REQUIRED = "At least one category must be provided"; 
    public static final String CATEGORY_NAME_REQUIRED = "Category name cannot be empty";
    public static final String CATEGORY_NAME_LENGTH = "Category name must be less than 50 characters";


    // ERROR USER 
    public static final String USER_EMAIL_REQUIRED = "Valid Email is required"; 
    public static final String USER_EMAIL_NOT_VALID = "Email format is invalid";
    public static final String USER_EMAIL_ALREADY_EXISTS = "User email already exists";
    public static final String USER_FIRST_NAME_REQUIRED = "First name is required";
    public static final String USER_LAST_NAME_REQUIRED = "Last name is required";
    public static final String USER_PASSWORD_REQUIRED = "Password is required";
    public static final String USER_NOT_FOUND = "User is not found";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ACCESS_DENIED = "Access denied: resource does not belong to the current user.";


    // BUDGET 
    public static final String BUDGET_NOT_FOUND = "Budget not found"; 
    public static final String BUDGET_ALREADY_EXISTS = "Budget already exists for this month";
    public static final String BUDGET_LIMIT_INVALID = "Budget limit must be positive or 0";
    public static final String BUDGET_ACCESS_DENIED = "You cannot access to this budget";
    public static final String BUDGET_NAME_REQUIRED = "Budget must have a name"; 
    public static final String BUDGET_NOT_NULL = "Budget cannot be null";
    public static final String BUDGET_DATE_IN_FUTURE = "Date cannot be in the future";
    public static final String BUDGET_REQUIRED = "Budget is required";

    
    // GENERIC ERROR 
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String XSS_CONTENT_NOT_ALLOWED = "HTML/JavaScript content is not allowed in field: ";

    // SAVINGS GOAL
    public static final String SAVINGS_GOAL_NOT_FOUND = "Savings goal not found";
    public static final String SAVINGS_GOAL_NAME_REQUIRED = "Savings goal must have a name";
    public static final String SAVINGS_GOAL_TARGET_REQUIRED = "Target amount is required";
    public static final String SAVINGS_GOAL_TARGET_INVALID = "Target amount must be positive";

}


