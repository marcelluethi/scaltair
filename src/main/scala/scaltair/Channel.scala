package scaltair

sealed trait Channel(
    fieldName: String,
    fieldType: FieldType
)

object Channel:
  case class X(
      fieldName: String,
      fieldType: FieldType,
      bins: Option[Bin] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName, fieldType):

    def scale(scale: Scale): Channel.X =
      Channel.X(fieldName, fieldType, bins, Some(scale), axis)

    def axis(axis: Axis): Channel.X =
      Channel.X(fieldName, fieldType, bins, scale, Some(axis))

    def binned(): Channel.X =
      Channel.X(fieldName, fieldType, Some(Bin.Auto), scale, axis)

    def binned(maxBins: Int): Channel.X =
      Channel.X(fieldName, fieldType, Some(Bin.MaxBins(maxBins)), scale, axis)

  case class Y(
      fieldName: String,
      fieldType: FieldType,
      agg: Option[AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName, fieldType):

    def count(): Channel =
      Channel.Y(
        fieldName,
        fieldType,
        Some(AggregateType.Count),
        scale
      )

    def scale(scale: Scale): Channel.Y =
      Channel.Y(fieldName, fieldType, agg, Some(scale))

    def axis(axis: Axis): Channel.Y =
      Channel.Y(fieldName, fieldType, agg, scale, Some(axis))

  case class Y2(
      fieldName: String,
      fieldType: FieldType,
      agg: Option[AggregateType] = None
  ) extends Channel(fieldName, fieldType):

    def count(): Channel =
      Channel.Y2(
        fieldName,
        fieldType
      )

    def scale(scale: Scale): Channel.Y2 =
      Channel.Y2(fieldName, fieldType, agg)

    def axis(axis: Axis): Channel.Y2 =
      Channel.Y2(fieldName, fieldType, agg)

  case class Color(fieldName: String, fieldType: FieldType)
      extends Channel(fieldName, fieldType)
  case class Size(fieldName: String)
      extends Channel(fieldName, FieldType.Quantitative)

enum AggregateType:
  case Count, Valid, Missing, Distinct, Sum, Mean, Average, Variance, VarianceP,
    Stdev, StdevP, Median, Q1, Q3, Modeskew, Min, Max, Argmin, Argmax, Ci0, Ci1,
    Cimin, Cimax, Cistderr, Ciquantile, Cilower, Cihigher, Cilimit, Nthvalue,
    Ntile, Lags, Lead, Lag, Percentrank, CumeDist, FirstValue, LastValue,
    NthValue

case class Scale(
    axisIncludesZero: Boolean = false,
    range: Option[Range] = None
):
  def range(range: Range): Scale = this.copy(range = Some(range))
  def axisIncludesZero(axisIncludesZero: Boolean): Scale =
    this.copy(axisIncludesZero = axisIncludesZero)

object Scale:
  def withRange(range: Range): Scale = Scale(range = Some(range))
  def includesZero: Scale = Scale(axisIncludesZero = true)

case class Axis(labelFontSize: Int = 14, titleFontSize: Int = 14)

enum Bin:
  case Auto extends Bin
  case MaxBins(maxBins: Int) extends Bin

enum FieldType:
  case Quantitative, Nominal, Ordinal, Temporal
