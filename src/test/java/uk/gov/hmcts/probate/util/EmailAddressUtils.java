package uk.gov.hmcts.probate.util;

public class EmailAddressUtils {
    //List of valid email addresses
    public static String[] VALID_EMAIL_ADDRESSES = {
        "email@example.com",
        "firstname.lastname@example.com",
        "email@subdomain.example.com",
        "firstname+lastname@example.com",
        "email@123.123.123.123",
        "1234567890@example.com",
        "email@example-one.com",
        "_______@example.com",
        "email@example.name",
        "email@example.museum",
        "email@example.co.jp",
        "firstname-lastname@example.com",
    };

    // List of invalid email addresses
    public static String[] INVALID_EMAIL_ADDRESSES = {
        "plainaddress",
        "#@%^%#$@#$@#.com",
        "@example.com",
        "Joe Smith <email@example.com>",
        "email.example.com",
        "email@example@example.com",
        ".email@example.com",
        "email.@example.com",
        "email..email@example.com",
        "あいうえお@example.com",
        "email@example.com (Joe Smith)",
        "email@example",
        "email@example..com",
        "Abc..123@example.com"
    };

    private EmailAddressUtils() {
    }
}