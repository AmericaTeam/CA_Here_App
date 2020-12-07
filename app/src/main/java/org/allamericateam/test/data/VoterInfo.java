package org.allamericateam.test.data;

public class VoterInfo {




    public boolean getRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDlId() {
        return dlId;
    }

    public void setDlId(String dlId) {
        this.dlId = dlId;
    }

    public String getSsnLast4Digit() {
        return ssnLast4Digit;
    }

    public void setSsnLast4Digit(String ssnLast4Digit) {
        this.ssnLast4Digit = ssnLast4Digit;
    }

    public DOB getDob() {
        return dob;
    }

    public void setDob(DOB dob) {
        this.dob = dob;
    }

    public boolean registered = false;
    public String firstName = "";
    public String middleName = "";
    public String lastName = "";
    public String email = "";
    public String dlId = "";
    public String ssnLast4Digit = "";
    public DOB dob;
    public boolean sentBallot = false;


    public VoterInfo(){

    }

    public VoterInfo(String s){

    }

    public VoterInfo(
            String firstName, String middleName, String lastName,
            String email, String dlId, String ssnLast4Digit,
            DOB dob){
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.dlId = dlId;
        this.ssnLast4Digit = ssnLast4Digit;
        this.dob = dob;
    }

    /*
    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("first_name", firstName);
        result.put("last_name", lastName);
        result.put("middle_name", middleName);
        //result.put("registered", isRegistered);
        result.put("role", "Registered Voter");
        result.put("sent_ballot", false);

        return result;
    }
*/

    public static class DOB{
        public String month = "";
        public String day = "";
        public String year = "";

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public DOB(){

        }

        public DOB(String MMM, String dd, String yyyy){
            month = MMM;
            day = dd;
            year = yyyy;
        }

        public DOB(int d, int m, int y){
            switch(m){
                case 1:
                    month = "JAN";
                    break;
                case 2:
                    month = "FEB";
                    break;
                case 3:
                    month = "MAR";
                    break;
                case 4:
                    month = "APR";
                    break;
                case 5:
                    month = "MAY";
                    break;
                case 6:
                    month = "JUN";
                    break;
                case 7:
                    month = "JUL";
                    break;
                case 8:
                    month = "AUG";
                    break;
                case 9:
                    month = "SEP";
                    break;
                case 10:
                    month = "OCT";
                    break;
                case 11:
                    month = "NOV";
                    break;
                case 12:
                    month = "DEC";
                    break;
            }

            if(d > 0 && d <10){
                day = "0" + d;
            }else{
                day = ""+d;
            }

            year = ""+y;
        }

    }
    public static class Builder{

        private boolean isRegistered = false;
        private String firstName = "";
        private String middleName = "";
        private String lastName = "";
        private String email = "";
        private String dlId = "";
        private String ssnLast4Digit = "";
        private DOB dob;

        public Builder(){

        }

        public Builder isRegistered(boolean val){
            isRegistered = val;
            return this;
        }

        public Builder firstName(String firstName){
            this.firstName = firstName;
            return this;
        }

        public Builder middleName(String middleName){
            this.middleName = middleName;
            return this;
        }

        public Builder lastName(String lastName){
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email){
            this.email = email;
            return this;
        }

        public Builder dlId(String dlId){
            this.dlId = dlId;
            return this;
        }

        public Builder ssnLast4Digit(String ssnLast4Digit){
            this.ssnLast4Digit = ssnLast4Digit;
            return this;
        }

        public Builder dob(DOB val){
            this.dob = val;
            return this;
        }

        public VoterInfo build(){
            return new VoterInfo(this);
        }


    }// end of Builder


    private VoterInfo(Builder builder){
        registered = builder.isRegistered;
        firstName = builder.firstName;
        middleName = builder.middleName;
        lastName = builder.lastName;
        email = builder.email;
        dlId = builder.dlId;
        ssnLast4Digit = builder.ssnLast4Digit;
    }

}
