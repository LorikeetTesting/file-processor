package fileprocessor

import cats.effect.{Effect, IO}
import fs2.Stream
import java.io.{BufferedReader, FileReader}

object FileProcessor {

  def processLines(path: String): Stream[IO, String] =
    Stream.bracket(IO(new BufferedReader(new FileReader(path))))(
      reader => Stream.eval(IO(reader.readLine())).repeat.takeWhile(_ != null),
      reader => IO(reader.close())
    )

  def countWords(path: String): Stream[IO, Int] =
    processLines(path).map(_.split("\\s+").length)

  def pipeline(inputPath: String, outputPath: String): Stream[IO, Unit] =
    Stream.bracket(IO(new java.io.PrintWriter(outputPath)))(
      writer => processLines(inputPath).evalMap(line => IO(writer.println(line.toUpperCase))),
      writer => IO(writer.close())
    )

}
