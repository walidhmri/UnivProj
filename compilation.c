#include <stdio.h>
#include <ctype.h>
#include <string.h>

#define START_STATE 0
#define IDENTIFIER_STATE 1
#define LITERAL_STATE 2
#define OPERATOR_STATE 3
#define ERROR_STATE 4

#define MAX_TOKEN_LENGTH 50

char *KEYWORDS[] = { "if", "else", "while", "int", "float" };
char *OPERATORS = "+-*/%()=<>";
char *WHITESPACE = " \t\n";

typedef struct {
    char value[MAX_TOKEN_LENGTH];
    char type[20];
    int position;
} Token;

int tokenize(char *input, Token *tokens, int max_tokens) {
    int state = START_STATE;
    int index = 0;
    int token_count = 0;
    char current_token[MAX_TOKEN_LENGTH] = { 0 };

    while (input[index] != '\0' && token_count < max_tokens) {
        char c = input[index];

        switch (state) {
            case START_STATE:
                if (isalpha(c)) {
                    state = IDENTIFIER_STATE;
                    strncpy(current_token, &c, 1);
                } else if (isdigit(c)) {
                    state = LITERAL_STATE;
                    strncpy(current_token, &c, 1);
                } else if (strchr(OPERATORS, c)) {
                    tokens[token_count].value[0] = c;
                    tokens[token_count].value[1] = '\0';
                    strcpy(tokens[token_count].type, "OPERATOR");
                    tokens[token_count].position = index;
                    token_count++;
                    state = START_STATE;
                } else if (strchr(WHITESPACE, c)) {
                    state = START_STATE;
                    current_token[0] = '\0';
                } else {
                    state = ERROR_STATE;
                    strncpy(current_token, &c, 1);
                }
                break;

            case IDENTIFIER_STATE:
                if (isalnum(c)) {
                    strncat(current_token, &c, 1);
                } else {
                    int i;
                    for (i = 0; i < sizeof(KEYWORDS) / sizeof(KEYWORDS[0]); i++) {
                        if (strcmp(current_token, KEYWORDS[i]) == 0) {
                            strcpy(tokens[token_count].type, "KEYWORD");
                            break;
                        }
                    }
                    if (i == sizeof(KEYWORDS) / sizeof(KEYWORDS[0])) {
                        strcpy(tokens[token_count].type, "IDENTIFIER");
                    }
                    strcpy(tokens[token_count].value, current_token);
                    tokens[token_count].position = index - strlen(current_token);
                    token_count++;
                    current_token[0] = '\0';
                    state = START_STATE;
                    index--;
                }
                break;

            case LITERAL_STATE:
                if (isdigit(c) || c == '.') {
                    strncat(current_token, &c, 1);
                } else {
                    strcpy(tokens[token_count].value, current_token);
                    strcpy(tokens[token_count].type, "LITERAL");
                    tokens[token_count].position = index - strlen(current_token);
                    token_count++;
                    current_token[0] = '\0';
                    state = START_STATE;
                    index--;
                }
                break;

            case ERROR_STATE:
                strcpy(tokens[token_count].value, current_token);
                strcpy(tokens[token_count].type, "ERROR");
                tokens[token_count].position = index - strlen(current_token);
                token_count++;
                current_token[0] = '\0';
                state = START_STATE;
                break;
        }

        index++;
    }

    if (current_token[0] != '\0') {
        if (state == IDENTIFIER_STATE) {
            int i;
            for (i = 0; i < sizeof(KEYWORDS) / sizeof(KEYWORDS[0]); i++) {
                if (strcmp(current_token, KEYWORDS[i]) == 0) {
                    strcpy(tokens[token_count].type, "KEYWORD");
                    break;
                }
            }
            if (i == sizeof(KEYWORDS) / sizeof(KEYWORDS[0])) {
                strcpy(tokens[token_count].type, "IDENTIFIER");
            }
        } else if (state == LITERAL_STATE) {
            strcpy(tokens[token_count].type, "LITERAL");
        } else if (state == ERROR_STATE) {
            strcpy(tokens[token_count].type, "ERROR");
        }
        strcpy(tokens[token_count].value, current_token);
        tokens[token_count].position = index - strlen(current_token);
        token_count++;
    }

    return token_count;
}

void handle_error(Token *token) {
    printf("Erreur %s : Caractère non reconnu à la position %d\n", token->value, token->position);
}

void print_report(Token *tokens, int token_count) {
    for (int i = 0; i < token_count; i++) {
        printf("Jeton : %s, Type : %s\n", tokens[i].value, tokens[i].type);
    }
}

int main() {
    char input[] = "int main() {\n int a = 5;\n float b = 3.14;\n}";
    Token tokens[100];
    int token_count = tokenize(input, tokens, 100);

    for (int i = 0; i < token_count; i++) {
        if (strcmp(tokens[i].type, "IDENTIFIER") == 0) {
        } else if (strcmp(tokens[i].type, "LITERAL") == 0) {
        } else if (strcmp(tokens[i].type, "OPERATOR") == 0) {
        } else if (strcmp(tokens[i].type, "KEYWORD") == 0) {
        } else if (strcmp(tokens[i].type, "ERROR") == 0) {
            handle_error(&tokens[i]);
        }
    }

    print_report(tokens, token_count);

    return 0;
}
