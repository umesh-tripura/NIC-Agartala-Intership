def is_armstrong_number(number):

    digits = str(number)
    num_digits = len(digits)
    
    sum_of_powers = sum(int(digit) ** num_digits for digit in digits)
    
    return sum_of_powers == number

number = int(input("Enter a number: "))

if is_armstrong_number(number):
    print(f"{number} is an Armstrong number.")
else:
    print(f"{number} is not an Armstrong number.")
