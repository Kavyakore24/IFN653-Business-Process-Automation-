package RepaymentCalculationCodelet;

import org.jdom2.Element;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.resourcing.codelets.AbstractCodelet;
import org.yawlfoundation.yawl.resourcing.codelets.CodeletExecutionException;

import java.util.ArrayList;
import java.util.List;

public class RepaymentCalculation extends AbstractCodelet {

    // This method is executed when the codelet is triggered
    @Override
    public Element execute(Element inputData, List<YParameter> inputVars, List<YParameter> outputVars)
            throws CodeletExecutionException {

        // Set the input and output parameters for the codelet
        setInputs(inputData, inputVars, outputVars);

        // Retrieve and parse loan-related information from input parameters
        String loanAmountString = (String) getParameterValue("LoanInfo/LoanAmount");
        double loanAmount = Double.parseDouble(loanAmountString);

        String annualInterestRateString = (String) getParameterValue("LoanInfo/AnnualInterestRate");
        double annualInterestRate = Double.parseDouble(annualInterestRateString);

        String loanTermInMonthsString = (String) getParameterValue("LoanInfo/NumberYears");
        int loanTermInMonths = Integer.parseInt(loanTermInMonthsString);

        // Calculate the monthly repayment amount
        double monthlyRepaymentAmount = calculateMonthlyRepayment(loanAmount, annualInterestRate, loanTermInMonths);

        // Set the calculated monthly repayment amount as an output parameter
        setParameterValue("MonthlyRepaymentAmount", String.valueOf(monthlyRepaymentAmount));

        // Return the output data
        return getOutputData();
    }

    // This method defines the required input and output parameters for the codelet
    @Override
    public List<YParameter> getRequiredParams() {
        List<YParameter> parameters = new ArrayList<>();

        // Define input parameters for LoanAmount, AnnualInterestRate, and NumberYears
        YParameter parameter = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        parameter.setDataTypeAndName("double", "LoanInfo/LoanAmount", "http://www.w3.org/2001/XMLSchema");
        parameters.add(parameter);

        parameter = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        parameter.setDataTypeAndName("double", "LoanInfo/AnnualInterestRate", "http://www.w3.org/2001/XMLSchema");
        parameters.add(parameter);

        parameter = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        parameter.setDataTypeAndName("int", "LoanInfo/NumberYears", "http://www.w3.org/2001/XMLSchema");
        parameters.add(parameter);

        // Define an output parameter for MonthlyRepaymentAmount
        parameter = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
        parameter.setDataTypeAndName("double", "MonthlyRepaymentAmount", "http://www.w3.org/2001/XMLSchema");
        parameters.add(parameter);

        return parameters;
    }

    // This method provides a description of what the codelet does
    @Override
    public String getDescription() {
        return "Calculates the monthly repayment amount";
    }

    // This is a helper method to perform the actual repayment calculation
    private double calculateMonthlyRepayment(double loanAmount, double annualInterestRate, int loanTermInMonths) {
        double monthlyInterestRate = annualInterestRate / 1200; // Convert annual rate to monthly rate
        double monthlyRepaymentAmount = loanAmount * (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTermInMonths))
                / (Math.pow(1 + monthlyInterestRate, loanTermInMonths) - 1);

        return Math.round(monthlyRepaymentAmount * 100.0) / 100.0; // Round to two decimal places
    }
}
