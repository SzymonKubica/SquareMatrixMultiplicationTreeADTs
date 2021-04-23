package generalmatrices.matrix;

import java.util.List;

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
