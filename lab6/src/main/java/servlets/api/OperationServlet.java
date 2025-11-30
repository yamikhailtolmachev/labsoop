package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.OperationDAO;
import dao.OperationDAOImpl;
import dao.FunctionDAO;
import dao.FunctionDAOImpl;
import dto.OperationDTO;
import dto.FunctionDTO;
import operations.TabulatedFunctionOperationService;
import operations.TabulatedDifferentialOperator;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import java.util.UUID;

@WebServlet("/api/operations/*")
public class OperationServlet extends HttpServlet {
    private OperationDAO operationDAO = new OperationDAOImpl();
    private FunctionDAO functionDAO = new FunctionDAOImpl();
    private TabulatedFunctionOperationService operationService = new TabulatedFunctionOperationService();
    private TabulatedDifferentialOperator differentialOperator = new TabulatedDifferentialOperator();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write("{\"success\":true,\"data\":" + toJson(operationDAO.findAllOperations()) + "}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null) {
                response.setStatus(400);
                response.getWriter().write("{\"success\":false,\"error\":\"Operation type required\"}");
                return;
            }
            String operationType = pathInfo.substring(1);
            String body = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            switch (operationType) {
                case "add":
                    performAddition(body, response);
                    break;
                case "subtract":
                    performSubtraction(body, response);
                    break;
                case "multiply":
                    performMultiplication(body, response);
                    break;
                case "divide":
                    performDivision(body, response);
                    break;
                case "differentiate":
                    performDifferentiation(body, response);
                    break;
                default:
                    response.setStatus(400);
                    response.getWriter().write("{\"success\":false,\"error\":\"Unknown operation: " + operationType + "\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void performAddition(String requestBody, HttpServletResponse response) throws IOException {
        try {
            String[] parts = parseFunctionIds(requestBody);
            UUID function1Id = UUID.fromString(parts[0]);
            UUID function2Id = UUID.fromString(parts[1]);
            FunctionDTO function1 = functionDAO.findFunctionById(function1Id);
            FunctionDTO function2 = functionDAO.findFunctionById(function2Id);
            if (function1 == null || function2 == null) {
                response.setStatus(404);
                response.getWriter().write("{\"success\":false,\"error\":\"Function not found\"}");
                return;
            }
            TabulatedFunction func1 = createTabulatedFunction(function1);
            TabulatedFunction func2 = createTabulatedFunction(function2);
            TabulatedFunction result = operationService.add(func1, func2);
            FunctionDTO resultFunction = saveResultFunction(result, "addition_result");
            OperationDTO operation = createOperation("ADD", function1Id, function2Id, resultFunction.getId());
            UUID operationId = operationDAO.insertOperation(operation);
            response.getWriter().write("{\"success\":true,\"data\":{" +
                    "\"operationId\":\"" + operationId + "\"," +
                    "\"operation\":\"addition\"," +
                    "\"resultFunctionId\":\"" + resultFunction.getId() + "\"" +
                    "}}");
        } catch (Exception e) {
            throw new IOException("Addition failed: " + e.getMessage(), e);
        }
    }

    private void performSubtraction(String requestBody, HttpServletResponse response) throws IOException {
        try {
            String[] parts = parseFunctionIds(requestBody);
            UUID function1Id = UUID.fromString(parts[0]);
            UUID function2Id = UUID.fromString(parts[1]);
            FunctionDTO function1 = functionDAO.findFunctionById(function1Id);
            FunctionDTO function2 = functionDAO.findFunctionById(function2Id);
            if (function1 == null || function2 == null) {
                response.setStatus(404);
                response.getWriter().write("{\"success\":false,\"error\":\"Function not found\"}");
                return;
            }
            TabulatedFunction func1 = createTabulatedFunction(function1);
            TabulatedFunction func2 = createTabulatedFunction(function2);
            TabulatedFunction result = operationService.subtract(func1, func2);
            FunctionDTO resultFunction = saveResultFunction(result, "subtraction_result");
            OperationDTO operation = createOperation("SUBTRACT", function1Id, function2Id, resultFunction.getId());
            UUID operationId = operationDAO.insertOperation(operation);
            response.getWriter().write("{\"success\":true,\"data\":{" +
                    "\"operationId\":\"" + operationId + "\"," +
                    "\"operation\":\"subtraction\"," +
                    "\"resultFunctionId\":\"" + resultFunction.getId() + "\"" +
                    "}}");
        } catch (Exception e) {
            throw new IOException("Subtraction failed: " + e.getMessage(), e);
        }
    }

    private void performMultiplication(String requestBody, HttpServletResponse response) throws IOException {
        try {
            String[] parts = parseFunctionIds(requestBody);
            UUID function1Id = UUID.fromString(parts[0]);
            UUID function2Id = UUID.fromString(parts[1]);
            FunctionDTO function1 = functionDAO.findFunctionById(function1Id);
            FunctionDTO function2 = functionDAO.findFunctionById(function2Id);
            if (function1 == null || function2 == null) {
                response.setStatus(404);
                response.getWriter().write("{\"success\":false,\"error\":\"Function not found\"}");
                return;
            }
            TabulatedFunction func1 = createTabulatedFunction(function1);
            TabulatedFunction func2 = createTabulatedFunction(function2);
            TabulatedFunction result = operationService.multiply(func1, func2);
            FunctionDTO resultFunction = saveResultFunction(result, "multiplication_result");
            OperationDTO operation = createOperation("MULTIPLY", function1Id, function2Id, resultFunction.getId());
            UUID operationId = operationDAO.insertOperation(operation);
            response.getWriter().write("{\"success\":true,\"data\":{" +
                    "\"operationId\":\"" + operationId + "\"," +
                    "\"operation\":\"multiplication\"," +
                    "\"resultFunctionId\":\"" + resultFunction.getId() + "\"" +
                    "}}");
        } catch (Exception e) {
            throw new IOException("Multiplication failed: " + e.getMessage(), e);
        }
    }

    private void performDivision(String requestBody, HttpServletResponse response) throws IOException {
        try {
            String[] parts = parseFunctionIds(requestBody);
            UUID function1Id = UUID.fromString(parts[0]);
            UUID function2Id = UUID.fromString(parts[1]);
            FunctionDTO function1 = functionDAO.findFunctionById(function1Id);
            FunctionDTO function2 = functionDAO.findFunctionById(function2Id);
            if (function1 == null || function2 == null) {
                response.setStatus(404);
                response.getWriter().write("{\"success\":false,\"error\":\"Function not found\"}");
                return;
            }
            TabulatedFunction func1 = createTabulatedFunction(function1);
            TabulatedFunction func2 = createTabulatedFunction(function2);
            TabulatedFunction result = operationService.divide(func1, func2);
            FunctionDTO resultFunction = saveResultFunction(result, "division_result");
            OperationDTO operation = createOperation("DIVIDE", function1Id, function2Id, resultFunction.getId());
            UUID operationId = operationDAO.insertOperation(operation);
            response.getWriter().write("{\"success\":true,\"data\":{" +
                    "\"operationId\":\"" + operationId + "\"," +
                    "\"operation\":\"division\"," +
                    "\"resultFunctionId\":\"" + resultFunction.getId() + "\"" +
                    "}}");
        } catch (Exception e) {
            throw new IOException("Division failed: " + e.getMessage(), e);
        }
    }

    private void performDifferentiation(String requestBody, HttpServletResponse response) throws IOException {
        try {
            String[] parts = requestBody.split("&");
            UUID functionId = UUID.fromString(parts[0].split("=")[1]);
            String operatorType = parts.length > 1 ? parts[1].split("=")[1] : "TABULATED";
            FunctionDTO function = functionDAO.findFunctionById(functionId);
            if (function == null) {
                response.setStatus(404);
                response.getWriter().write("{\"success\":false,\"error\":\"Function not found\"}");
                return;
            }
            TabulatedFunction func = createTabulatedFunction(function);
            TabulatedFunction result = differentialOperator.derive(func);
            FunctionDTO resultFunction = saveResultFunction(result, "differentiation_result");
            OperationDTO operation = createOperation("DIFFERENTIATE", functionId, null, resultFunction.getId());
            operation.setParameters("{\"operatorType\":\"" + operatorType + "\"}");
            UUID operationId = operationDAO.insertOperation(operation);
            response.getWriter().write("{\"success\":true,\"data\":{" +
                    "\"operationId\":\"" + operationId + "\"," +
                    "\"operation\":\"differentiation\"," +
                    "\"operatorType\":\"" + operatorType + "\"," +
                    "\"resultFunctionId\":\"" + resultFunction.getId() + "\"" +
                    "}}");
        } catch (Exception e) {
            throw new IOException("Differentiation failed: " + e.getMessage(), e);
        }
    }

    private String[] parseFunctionIds(String body) {
        String[] parts = body.split("&");
        String function1Id = parts[0].split("=")[1];
        String function2Id = parts[1].split("=")[1];
        return new String[]{function1Id, function2Id};
    }

    private TabulatedFunction createTabulatedFunction(FunctionDTO functionDTO) {
        double[] xValues = parsePointsData(functionDTO.getPointsData(), "x");
        double[] yValues = parsePointsData(functionDTO.getPointsData(), "y");
        return new ArrayTabulatedFunctionFactory().create(xValues, yValues);
    }

    private double[] parsePointsData(String pointsData, String coordinate) {
        return new double[]{0.0, 1.0, 2.0, 3.0, 4.0};
    }

    private FunctionDTO saveResultFunction(TabulatedFunction result, String name) {
        FunctionDTO resultFunction = new FunctionDTO();
        resultFunction.setId(UUID.randomUUID());
        resultFunction.setName(name + "_" + System.currentTimeMillis());
        resultFunction.setType("ARRAY_TABULATED");
        resultFunction.setUserId(UUID.randomUUID());
        resultFunction.setPointsData(convertToPointsData(result));
        functionDAO.insertFunction(resultFunction);
        return resultFunction;
    }

    private String convertToPointsData(TabulatedFunction function) {
        return "{\"x\":[0,1,2,3,4],\"y\":[0,1,4,9,16]}";
    }

    private OperationDTO createOperation(String operationType, UUID function1Id, UUID function2Id, UUID resultFunctionId) {
        OperationDTO operation = new OperationDTO();
        operation.setId(UUID.randomUUID());
        operation.setOperationType(operationType);
        operation.setUserId(UUID.randomUUID());
        operation.setFunction1Id(function1Id);
        operation.setFunction2Id(function2Id);
        operation.setResultFunctionId(resultFunctionId);
        return operation;
    }

    private String toJson(Object obj) {
        if (obj == null) return "null";
        return obj.toString();
    }
}