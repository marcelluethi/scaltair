package scalismo.plot

enum DataValue:
  case Nominal(value: String)
  case Quantitative(value: Double)

object Data:

  type ColumnName = String

  /** A map from column names to the data values in the column.
    */
  type ColumnData = Map[ColumnName, Seq[DataValue]]
