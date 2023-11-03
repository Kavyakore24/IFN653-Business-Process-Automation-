package ApplicationIdentifierCodelet;

import org.jdom2.Element;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.resourcing.codelets.AbstractCodelet;
import org.yawlfoundation.yawl.resourcing.codelets.CodeletExecutionException;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.yawlfoundation.yawl.unmarshal.YMetaData.dateFormat;

public class ApplicationID extends AbstractCodelet {

    // This is executed when the codelet is activated in the case
    @Override
    public Element execute(Element inputData, List<YParameter> inputVars, List<YParameter> outputVars)
            throws CodeletExecutionException {

        // Set the input and output parameters for the codelet
        setInputs(inputData, inputVars, outputVars);

        // Get the unique identifier for the process instance
        String processInstanceID = getWorkItem().getRootCaseID();

        // Retrieve the DateOfBirth parameter and format it as a string, and extract the year
        Date dobdate = (Date) getParameterValue("DateOfBirth");
        String applicantDateOfBirth = dateFormat.format(dobdate);
        String applicantBirthYear = applicantDateOfBirth.split("-")[0];

        // Generate a unique application identifier based on processInstanceID and applicantBirthYear
        String applicationIdentifier = generateApplicationIdentifier(processInstanceID, applicantBirthYear);

        // Set the generated application identifier as an output parameter
        setParameterValue("ApplicationIdentifier", applicationIdentifier);

        // Return the output
        return getOutputData();
    }

    // This defines the required input and output parameters for the codelet
    @Override
    public List<YParameter> getRequiredParams() {
        List<YParameter> parameters = new ArrayList<>();

        // Define an input parameter for DateOfBirth
        YParameter parameter = new YParameter(null, YParameter._INPUT_PARAM_TYPE);
        parameter.setDataTypeAndName("string", "DateOfBirth",
                "http://www.w3.org/2001/XMLSchema");
        parameters.add(parameter);

        // Define an output parameter for ApplicationIdentifier
        parameter = new YParameter(null, YParameter._OUTPUT_PARAM_TYPE);
        parameter.setDataTypeAndName("string", "ApplicationIdentifier",
                "http://www.w3.org/2001/XMLSchema");
        parameters.add(parameter);

        return parameters;
    }

    // This provides a description of what the codelet does
    @Override
    public String getDescription() {
        return "Generates a unique loan application identifier";
    }

    // This is a helper method to generate a unique application identifier
    private String generateApplicationIdentifier(String processInstanceID, String applicantBirthYear) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");

        Date currentDate = new Date();
        String monthCode = String.valueOf((char) (Integer.parseInt(monthFormat.format(currentDate)) + 64));
        String yearCode = yearFormat.format(currentDate);

        int ageDifference = Integer.parseInt(yearCode) - Integer.parseInt(applicantBirthYear) + 2000;
        String ageDifferenceCode = String.format("%02d", ageDifference);

        String processIDCode = String.format("%05d", Integer.parseInt(processInstanceID));

        return processIDCode + "/" + monthCode + "/" + yearCode + "/" + ageDifferenceCode;
    }
}