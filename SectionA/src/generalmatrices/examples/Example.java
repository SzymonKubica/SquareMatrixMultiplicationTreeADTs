package generalmatrices.examples;

import generalmatrices.matrix.Matrix;
import generalmatrices.pair.PairWithOperators;

import java.util.List;

public class Example {

  public static Matrix<PairWithOperators> multiplyPairMatrices(
          List<Matrix<PairWithOperators>> matrices) {

    return matrices.stream().reduce((matrix1, matrix2) -> matrix1.product(matrix2,
            PairWithOperators::sum, PairWithOperators::product)).get();
  }

}
