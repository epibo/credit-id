package com.creditid.cid.utils

import java.io._

import cats.effect.concurrent.Semaphore
import cats.effect.{Concurrent, Resource, Sync}
import cats.implicits._

/**
 * @author Wei.Chou
 * @version 1.0, 15/11/2019
 */
object FlagFile {
  def writeFlag[F[_] : Sync : Concurrent](fileName: String, valueInLine: String): F[Unit] = {
    val f = new File(fileName)
    for {
      guard <- Semaphore[F](1)
      _ <- writer(f, guard).use { writer =>
        guard.withPermit(Sync[F].delay(writer.write(valueInLine)))
      }
    } yield ()
  }

  def readerFlag[F[_] : Sync : Concurrent](fileName: String): F[String] = {
    val f = new File(fileName)
    for {
      guard <- Semaphore[F](1)
      line <- reader(f, guard).use { reader =>
        guard.withPermit(Sync[F].delay(reader.readLine))
      }
    } yield line
  }

  private def reader[F[_] : Sync](f: File, guard: Semaphore[F]): Resource[F, BufferedReader] =
    Resource.make {
      Sync[F].delay(new BufferedReader(new InputStreamReader(new FileInputStream(f))))
    } { reader =>
      guard.withPermit {
        Sync[F].delay(reader.close()).handleErrorWith(_ => Sync[F].unit)
      }
    }

  private def writer[F[_] : Sync](f: File, guard: Semaphore[F]): Resource[F, BufferedWriter] =
    Resource.make {
      Sync[F].delay(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f))))
    } { writer =>
      guard.withPermit {
        Sync[F].delay(writer.close()).handleErrorWith(_ => Sync[F].unit)
      }
    }
}
