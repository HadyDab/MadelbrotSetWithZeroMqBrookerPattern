/**
 * 
 */
package loadbalancingbroker.zmqutils;

/**
 * @author User
 *
 */
public class Complex {
	private double creal;
	private double cimaginary;

	public Complex(double creal, double cimaginary) {
		this.creal = creal;
		this.cimaginary = cimaginary;
	}

	public Complex(double creal) {
		this(creal, 0);
	}

	public Complex() {
		this(0, 0);
	}

	/**
	 * Addition of Complex number with then help of the formula (a+ b*i)+ (c +d*i) =
	 * ((a+c)+(b+d)*i)
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static Complex add(Complex c1, Complex c2) {
		return c1.add(c2);
	}

	/**
	 * Subtraction of Complex number with the help of the formula (a+ b*i)+ (c +d*i)
	 * = ((a-c)+(b-d)*i)
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static Complex subtract(Complex c1, Complex c2) {
		return c1.minus(c2);
	}

	/**
	 * Multiplication of Complex with the help of the formula (+- x +- y*i)(+- u +-
	 * v*i) = (x*u - y*v) + (x*v + y*u)
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static Complex multiply(Complex c1, Complex c2) {
		return c1.times(c2);
	}

	public Complex conjugate() {
		return new Complex(creal, -cimaginary);
	}

	public Complex reciprocal() {
		double scale = creal * creal + cimaginary * cimaginary;
		return new Complex(creal / scale, -cimaginary / scale);
	}

	public void squared() {
		timesThis(this);
	}

	public Complex squared(Complex c) {
		return c.times(c);
	}

	private void timesThis(Complex c) {
		double realPart = creal * c.getCreal() - cimaginary * c.getCimaginary();
		double imaginaryPart = creal * c.getCimaginary() + cimaginary * c.getCreal();
		c.setCreal(realPart);
		c.setCimaginary(imaginaryPart);
	}

	private Complex times(Complex c) {
		double realPart = creal * c.getCreal() - cimaginary * c.getCimaginary();
		double imaginaryPart = creal * c.getCimaginary() + cimaginary * c.getCreal();
		return new Complex(realPart, imaginaryPart);
	}

	private Complex minus(Complex c) {
		return new Complex(creal - c.getCreal(), cimaginary - c.getCimaginary());
	}

	private Complex add(Complex c) {
		return new Complex(creal + c.getCreal(), cimaginary + c.getCimaginary());
	}

	public double abs() {
		return Math.sqrt((creal * creal) + (cimaginary * cimaginary));
	}

	public void addConstant(double costant) {
		creal += costant;
		cimaginary += costant;
	}

	public double getCreal() {
		return creal;
	}

	public void setCreal(double creal) {
		this.creal = creal;
	}

	public double getCimaginary() {
		return cimaginary;
	}

	public void setCimaginary(double cimaginary) {
		this.cimaginary = cimaginary;
	}

	@Override
	public String toString() {
		return creal + "" + (cimaginary < 0 ? "" : "+") + cimaginary + "i";
	}

}
