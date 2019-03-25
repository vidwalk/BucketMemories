package com.example.bucketnotes.bucketmemories.validators;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;



public class Validator<T> {

    private Set<Rule<T>> rules;
    private ArrayList<String> messages;

    private abstract static class Rule<T> {

        protected String message;

        public Rule (String message) {
            this.message = message;
        }

        public abstract boolean validate(T value);
    }

    private abstract static class CriteriaRule<T, C> extends Rule<T> {

        protected C criteria;

        public CriteriaRule(String message, C criteria) {
            super(message);
            this.criteria = criteria;
        }
    }

    private Validator() {
        rules = new HashSet<>();
        messages = new ArrayList<>();
    }

    public String getLastMessage() {
        return messages.isEmpty() ? null : messages.get(messages.size() - 1);
    }

    public boolean validate (T value, String...fieldName) {
        boolean result = true;
        messages.clear();
        for (Rule<T> rule: rules) {
            if (!rule.validate(value)) {
                result = false;
                String message = rule.message;
                if (fieldName != null && fieldName.length > 0) {
                    message = String.format("%s %s", fieldName[0], rule.message);
                }
                messages.add(message);
            }
        }
        return result;
    }

    public static class StringValidatorBuilder {

        Validator<String> validator;
        private Rule<String> notEmptyRule;
        private Rule<String> notEmptyOrWhiteSpaceRule;
        private CriteriaRule<String, Integer> minLengthRule;

        private Rule<String> getNotEmptyRule(String...messages) {
            String message = getMessageString(messages, "cannot be blank");
            if (notEmptyRule != null) {
                notEmptyRule.message = message;
            } else {
                notEmptyRule = new Rule<String>(message) {
                    @Override
                    public boolean validate(String value) {
                        return !TextUtils.isEmpty(value);
                    }
                };
            }
            return notEmptyRule;
        }

        private CriteriaRule<String, Integer> getMinLengthRule(Integer value, String...messages) {
            String message = getMessageString(messages,
                    String.format("must contain more then %d symbols", value));
            if (minLengthRule != null) {
                minLengthRule.message = message;
            } else {
                minLengthRule = new CriteriaRule<String, Integer>(message, value) {
                    @Override
                    public boolean validate(String value) {
                        return !TextUtils.isEmpty(value) && value.length() >= this.criteria;
                    }
                };
            }
            return minLengthRule;
        }

        private Rule<String> getNotEmptyOrWhiteSpaceRule(String...messages) {
            String message = getMessageString(messages, "cannot be blank");
            if (notEmptyOrWhiteSpaceRule != null) {
                notEmptyOrWhiteSpaceRule.message = message;
            } else {
                notEmptyOrWhiteSpaceRule = new Rule<String>(message) {
                    @Override
                    public boolean validate(String value) {
                        return !TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim());
                    }
                };
            }
            return notEmptyOrWhiteSpaceRule;
        }

        private String getMessageString(String[] messages, String defaultMessage) {
            String message = "";
            if (messages == null || messages.length == 0) {
                message = defaultMessage;
            } else {
                message = messages[0];
            }
            return message;
        }

        public StringValidatorBuilder() {
            validator = new Validator<String>();
        }

        public StringValidatorBuilder setNotEmpty(String...message) {
            validator.rules.add(getNotEmptyRule(message));
            return this;
        }

        public StringValidatorBuilder setMinLength(int minLength, String...message) {
            validator.rules.add(getMinLengthRule(minLength, message));
            return this;
        }

        public Validator<String> build() {
            return validator;
        }
    }

    public static class NumberValidatorBuilder<N extends Number & Comparable<? super N>> {

        Validator<N> validator;
        private CriteriaRule<N, N> notLessThenMin;
        private CriteriaRule<N, N> notMoreThanMax;

        private CriteriaRule<N, N> getNotLessThanMin(N value, String...messages) {
            String message = getMessageString(messages,
                    String.format("value must be more then " + value));
            if (notLessThenMin != null) {
                notLessThenMin.message = message;
            } else {
                notLessThenMin = new CriteriaRule<N, N>(message, value) {
                    @Override
                    public boolean validate(N value) {
                        return (value.compareTo(this.criteria) >= 0);
                    }
                };
            }
            return notLessThenMin;
        }

        private CriteriaRule<N, N> getNotMoreThanMax(N value, String...messages) {
            String message = getMessageString(messages,
                    String.format("value must be less then " + value));
            if (notMoreThanMax != null) {
                notMoreThanMax.message = message;
            } else {
                notMoreThanMax = new CriteriaRule<N, N>(message, value) {
                    @Override
                    public boolean validate(N value) {
                        return (value.compareTo(this.criteria) <= 0);
                    }
                };
            }
            return notMoreThanMax;
        }

        private String getMessageString(String[] messages, String defaultMessage) {
            String message;
            if (messages == null || messages.length == 0) {
                message = defaultMessage;
            } else {
                message = messages[0];
            }
            return message;
        }

        public NumberValidatorBuilder() {
            validator = new Validator<>();
        }

        public NumberValidatorBuilder setMin(N minValue, String...message) {
            validator.rules.add(getNotLessThanMin(minValue, message));
            return this;
        }

        public NumberValidatorBuilder setToRange(N minValue, N maxValue, String...messages) {
            if (minValue.compareTo(maxValue) < 0) {
                setMin(minValue, messages);
                setMax(maxValue, messages);
            } else {
                setMin(maxValue, messages);
                setMax(minValue, messages);
            }
            return this;
        }

        public NumberValidatorBuilder setMax(N maxValue, String...message) {
            validator.rules.add(getNotMoreThanMax(maxValue, message));
            return this;
        }

        public Validator<N> build() {
            return validator;
        }
    }

    public static class DateValidatorBuilder {

        Validator<Date> validator;
        private CriteriaRule<Date, Date> notLessThenMinDate;
        private CriteriaRule<Date, Date> notMoreThanMaxDate;

        private CriteriaRule<Date, Date> getNotMoreThanMaxDate(Date date,
                                                                       String...messages) {
            String dateString = date.toString();
            String message = getMessageString(messages,
                    String.format("date must be less then ", dateString));
            if (notMoreThanMaxDate  != null) {
                notMoreThanMaxDate.message = message;
            } else {
                notMoreThanMaxDate = new CriteriaRule<Date, Date>(message, date) {
                    @Override
                    public boolean validate(Date date) {
                        return (date.before(this.criteria));
                    }
                };
            }
            return notMoreThanMaxDate;
        }

        private CriteriaRule<Date, Date> getNotLessThanMinDate(Date date,
                                                                       String...messages) {
            final String dateString = date.toString();
            String message = getMessageString(messages,
                    String.format("date must be more then " + dateString));
            if (notLessThenMinDate != null) {
                notLessThenMinDate.message = message;
            } else {
                notLessThenMinDate = new CriteriaRule<Date, Date>(message, date) {
                    @Override
                    public boolean validate(Date date) {
                        return date.after(this.criteria);
                    }
                };
            }
            return notLessThenMinDate;
        }

        private String getMessageString(String[] messages, String defaultMessage) {
            String message = "";
            if (messages == null || messages.length == 0) {
                message = defaultMessage;
            } else {
                message = messages[0];
            }
            return message;
        }

        public DateValidatorBuilder() {
            validator = new Validator<>();
        }

        public DateValidatorBuilder setMin(Date minDate, String...message) {
            validator.rules.add(getNotLessThanMinDate(minDate, message));
            return this;
        }

        public DateValidatorBuilder setToRange(Date minDate, Date maxDate,
                                               String...messages) {
            if (minDate.compareTo(maxDate) < 0) {
                setMin(minDate, messages);
                setMax(maxDate, messages);
            } else {
                setMin(maxDate, messages);
                setMax(minDate, messages);
            }
            return this;
        }

        public DateValidatorBuilder setMax(Date maxDate, String...message) {
            validator.rules.add(getNotMoreThanMaxDate(maxDate, message));
            return this;
        }

        public Validator<Date> build() {
            return validator;
        }
    }
}
