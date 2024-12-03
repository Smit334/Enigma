# Enigma Simulator

This project simulates the Enigma machine, a cipher device used during World War II. The simulator allows for encryption and decryption of messages using various rotor configurations.

## Usage

To run the Enigma simulator, use the following command:
```
java enigma.Main [--verbose] CONFIGURATION_FILE [INPUT] [OUTPUT]
```

- `--verbose`: Optional flag to enable verbose output.
- `CONFIGURATION_FILE`: The configuration file specifying the rotors and their settings.
- `INPUT`: Optional input file containing messages. If not provided, input is taken from standard input.
- `OUTPUT`: Optional output file for processed messages. If not provided, output is written to standard output.

## Configuration

The simulator requires a configuration file to specify the rotors and their settings. The configuration file should be provided as the first argument.

### Configuration File Format

The configuration file should contain the following information in order:

1. **Alphabet**: A string representing the alphabet used by the Enigma machine.
2. **Number of Rotors**: An integer specifying the total number of rotors.
3. **Number of Pawls**: An integer specifying the number of pawls (rotors that can move).
4. **Rotor Descriptions**: Each rotor description should include:
    - Rotor name
    - Rotor notches
    - Rotor permutation cycles

## Running Tests

To run the tests, use the provided scripts:

- `test-error`: Runs each file through `java enigma.Main` and checks for non-zero exit codes and no exception backtrace.
- `test-correct`: Runs each input file through `java enigma.Main` and compares the output to the expected output.

Example usage:
```
make test-error
make test-correct
```

## Makefile Targets

- `default`: Compiles the Enigma simulator.
- `style`: Runs the style checker on the project source files.
- `check`: Compiles the simulator and runs the tests.
- `clean`: Removes all generated files and testing output files.

To compile and test the project, use:
```
make
make check
```

To clean up the project directory, use:
```
make clean
```

## Project Structure

The project is organized as follows:

- `enigma/`: Contains the main source code for the Enigma simulator.
- `testing/`: Contains test files and scripts for running tests.
- `Makefile`: Defines the build and test targets for the project.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## Contact

For any questions or inquiries, please contact the project maintainer at [Smit334](https://github.com/Smit334).
