package generalmatrices.matrix;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;

public class Matrix<T> {
  private final int order;
  private final T[][] matrix;

  public Matrix(List<T> source) {
    if (source.isEmpty()) {
      throw new IllegalArgumentException();
    } else {
      order = (int) Math.sqrt(source.size());
      matrix = (T[][]) new Object[order][order];
      populateMatrix(source);
    }
  }

  private void populateMatrix(List<T> source) {
    int sourcePointer = 0;
    for (int i = 0; i < order; i++) {
      for (int j = 0; j < order; j++) {
        matrix[i][j] = source.get(sourcePointer);
        sourcePointer++;
      }
    }
  }

  public T get(int row, int col) {
    return matrix[row][col];
  }

  public int getOrder() {
    return order;
  }

  public Matrix<T> sum(Matrix<T> other, BinaryOperator<T> elementSum) {
    List<T> resultingListOfSums = new LinkedList<>();
    for (int i = 0; i < getOrder(); i++) {
      for (int j = 0; j < getOrder(); j++) {
        resultingListOfSums.add(elementSum.apply(get(i, j), other.get(i, j)));
      }
    }
    return new Matrix<>(resultingListOfSums);
  }

  public Matrix<T> product(Matrix<T> other,
                           BinaryOperator<T> elementSum,
                           BinaryOperator<T> elementProduct
  ) {
    List<T> resultingListOfProducts = new LinkedList<>();
    for (int i = 0; i < getOrder(); i++) {
      for (int j = 0; j < getOrder(); j++) {
        resultingListOfProducts
                .add(computeIJMatrixProductEntry(i, j, other, elementSum, elementProduct));
      }
    }
    return new Matrix<>(resultingListOfProducts);
  }

  private T computeIJMatrixProductEntry(int row,
                                        int column,
                                        Matrix<T> other,
                                        BinaryOperator<T> elementSum,
                                        BinaryOperator<T> elementProduct
  ) {
    List<T> products = new LinkedList<>();
    for (int i = 0; i < getOrder(); i++) {
      products.add(elementProduct.apply(get(row, i), other.get(i, column)));
    }
    return products.stream().reduce(elementSum).get();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < order; i++) {
      sb.append(getRowStringRepresentation(i));
    }
    sb.append("]");
    return sb.toString();
  }

  private String getRowStringRepresentation(int row) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < getOrder(); i++) {
      sb.append(get(row, i));
      if (i != getOrder() - 1) {
        sb.append(" ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

}
